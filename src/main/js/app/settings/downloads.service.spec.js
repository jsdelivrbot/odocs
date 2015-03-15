'use strict';

describe('download.service.spec.js', function() {
  var anyId = 'anyId';

  var $httpBackend;
  var apiUrl;
  var stompServiceMock;

  var createService;
  var websocketMock;
  var documentationChangeServiceMock;

  beforeEach(function() {
    documentationChangeServiceMock = {
      documentationChange: jasmine.createSpy('documentation change')
    };

    module('docs.settings', 'docs.test', function($provide) {
      $provide.value('documentationChangeService', documentationChangeServiceMock);
    });
    inject(function(_$httpBackend_, _stomp_, $injector, API) {
      $httpBackend = _$httpBackend_;
      createService = function() {
        return $injector.get('downloadsService');
      };
      stompServiceMock = _stomp_;
      apiUrl = API.url;
    });

    websocketMock = {
      subscribe: jasmine.createSpy('subscribeSpy')
    };
  });

  describe('actions spec', function() {
    var service;
    var listener;
    beforeEach(function() {
      service = createServiceWithoutData();
      listener = getEventListener();

      sendEvent(listener, newEvent(anyId, 'SUBMIT'));
    });

    runParametrizedTest(
      function(actionType, propertyName) {
        return 'should send action ' + actionType + ' for item and update property ' + propertyName;
      },
      [
        ['REMOVE', 'removeRequested', 'requestRemove'],
        ['ABORT', 'abortRequested', 'requestAbort']
      ],
      function(actionType, propertyName, actionFnName) {
        $httpBackend
          .expectPOST(apiUrl('/feeds/downloads/' + anyId + '/actions'), {action: actionType})
          .respond(200);

        //then
        service[actionFnName](anyId)

          //when
          .then(function(removedItem) {
            expect(removedItem.id).toEqual(anyId);
            expect(removedItem[propertyName]).toEqual(true);
          });

        $httpBackend.flush();
      });

    function createServiceWithoutData() {
      $httpBackend
        .expectGET(apiUrl('/feeds/downloads'))
        .respond(200, []);

      var result = createService();

      $httpBackend.flush();
      stompServiceMock.connect.deferred.resolveAndApply(websocketMock);

      return result;
    }
  });

  describe('notify events handling', function() {
    var downloadingItems;
    var listener;

    beforeEach(function() {
      $httpBackend
        .expectGET(apiUrl('/feeds/downloads'))
        .respond(200, []);

      createService()
        .downloadingItems()
        .then(function(items) {
          expect(items).toEqual([]);
          downloadingItems = items;
        });


      $httpBackend.flush();
      stompServiceMock.connect.deferred.resolveAndApply(websocketMock);

      expect(websocketMock.subscribe).toHaveBeenCalledWith('/feeds/downloads/events', jasmine.any(Function));
      listener = getEventListener();
    });

    it('should add new event to collection', function() {
      sendEvent(listener, newEvent(anyId, 'SUBMIT'));

      expect(downloadingItems.length).toEqual(1);
      expectItem(downloadingItems[0], {
        id: anyId,
        latestEvent: newEvent(anyId, 'SUBMIT'),
        SUBMIT: 'SUBMIT'
      });
    });

    it('should remove event from collection on remove', function() {
      sendEvent(listener, newEvent(anyId, 'SUBMIT'));
      expect(downloadingItems.length).toEqual(1);

      sendEvent(listener, newEvent(anyId, 'REMOVE'));
      expect(downloadingItems.length).toEqual(0);
    });

    it('should not fail when event not found in collection', function() {
      sendEvent(listener, newEvent('unknown event', 'FINISH'));
      expect(downloadingItems.length).toEqual(0);
    });

    it('should notify documentation change service on download finish', function() {
      sendEvent(listener, newEvent(anyId, 'SUBMIT'));
      sendEvent(listener, newEvent(anyId, 'FINISH'));

      expect(documentationChangeServiceMock.documentationChange).toHaveBeenCalled();
    });

    runParametrizedTest(
      'should update event state',
      [
        [newEvent(anyId, 'START')],
        [newEvent(anyId, 'PROGRESS')],
        [newEvent(anyId, 'FINISH')],
        [newEvent(anyId, 'ERROR')],
        [newEvent(anyId, 'ABORT')]],
      function(latestEvent) {
        sendEvent(listener, newEvent(anyId, 'SUBMIT'));
        expect(downloadingItems.length).toEqual(1);

        sendEvent(listener, latestEvent);

        expect(downloadingItems.length).toEqual(1);
        var item = downloadingItems[0];
        expect(item[latestEvent.eventType]).toEqual(latestEvent.eventType);
        expect(item.latestEvent).toEqual(latestEvent);
      });
  });

  describe('initial events loading', function() {
    it('should load all events on service start', function() {
      $httpBackend
        .expectGET(apiUrl('/feeds/downloads'))
        .respond(200, []);

      //when
      createService();

      //then
      $httpBackend.flush();
    });

    it('should process initial events on start', function() {
      $httpBackend
        .expectGET(apiUrl('/feeds/downloads'))
        .respond(200, [{
          id: anyId,
          abortRequested: false,
          removeRequested: true,
          submitEvent: eventWithoutId('SUBMIT'),
          startEvent: eventWithoutId('START'),
          progressEvent: eventWithoutId('PROGRESS'),
          errorEvent: eventWithoutId('ERROR')
        }]);

      //when
      var downloadsService = createService();

      //then
      downloadsService
        .downloadingItems()
        .then(function(items) {

          expect(items.length).toEqual(1);
          expectItem(items[0], {
            id: anyId,
            latestEvent: newEvent(anyId, 'ERROR'),
            abortRequested: false,
            removeRequested: true,
            SUBMIT: 'SUBMIT',
            START: 'START',
            PROGRESS: 'PROGRESS',
            ERROR: 'ERROR'
          });
        });

      $httpBackend.flush();
      stompServiceMock.connect.deferred.resolveAndApply(websocketMock);
    });
  });

  runParametrizedTest(
    function(eventType, expectedStates) {
      return 'should detect event type ' + JSON.stringify(eventType) + ' as ' + expectedStates;
    }, [
      [['isWaiting',     'isPending'],  {submitEvent: eventWithoutId('SUBMIT') } ],
      [['isWaiting',     'isPending'],  {submitEvent: eventWithoutId('SUBMIT'), startEvent: eventWithoutId('START')}],
      [['isInProgress',  'isPending'],  {submitEvent: eventWithoutId('SUBMIT'), progressEvent: eventWithoutId('PROGRESS')}],
      [['hasError',      'isFinished'], {submitEvent: eventWithoutId('SUBMIT'), errorEvent: eventWithoutId('ERROR')}],
      [['hasSuccess',    'isFinished'], {submitEvent: eventWithoutId('SUBMIT'), finishEvent: eventWithoutId('FINISH')}],
      [['isAborted',     'isFinished'], {submitEvent: eventWithoutId('SUBMIT'), abortEvent: eventWithoutId('ABORT')}],
    ],
    function(expectedStates, event) {
      var otherStates = _.xor(
        ['isWaiting', 'isInProgress', 'isPending', 'hasSuccess', 'hasError', 'isFinished', 'isAborted'],
        expectedStates);
      $httpBackend
        .expectGET(apiUrl('/feeds/downloads'))
        .respond(200, [_.assign(event, {id: anyId})]);

      var service = createService();

      service
        .downloadingItems()
        .then(function(items) {
          var item = items[0];
          _.forEach(expectedStates, function(expectedState) {
            expect(item[expectedState]()).toEqual(true);
          });
          _.forEach(otherStates, function(state) {
            expect(item[state]()).toEqual(false);
          });
        });

      $httpBackend.flush();
      stompServiceMock.connect.deferred.resolveAndApply(websocketMock);
    });

  function expectItem(actualItem, expectedItem) {
    var itemWithoutFunctions = _.omit(actualItem, 'isWaiting', 'isInProgress', 'isPending', 'hasSuccess', 'hasError', 'isAborted', 'isFinished');
    expect(itemWithoutFunctions).toEqual(expectedItem);
  }

  function newEvent(id, type) {
    var result = {
      id: id,
      eventType: type
    };
    result[type] = type;
    return result;
  }

  function eventWithoutId(type) {
    return _.omit(newEvent('anyIdToBeRemoved', type), 'id');
  }

  function getEventListener() {
    return websocketMock.subscribe.calls.mostRecent().args[1];
  }

  function sendEvent(listener, event) {
    listener({body: JSON.stringify(event)});
  }
});
