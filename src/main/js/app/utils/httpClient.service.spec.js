'use strict';

describe('httpClient.service.spec.js', function() {
  var _;
  var $httpBackend;
  var apiUrl;
  var httpClient;

  beforeEach(module('docs.utils'));
  beforeEach(inject(function(_httpClient_, _$httpBackend_, API, ___) {
    httpClient = _httpClient_;
    $httpBackend = _$httpBackend_;
    _ = ___;
    apiUrl = API.url;
  }));

  afterEach(function() {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  describe('httpClient.get', function() {
    it('should unwrap http response data', function() {
      $httpBackend.expectGET(apiUrl('/example'))
        .respond(200, sampleResponse());

      httpClient.get('/example')
        .then(expectSampleResponse, expectNoError);

      $httpBackend.flush();
    });

    it('should pass all get params', function() {
      $httpBackend.expectGET(apiUrl('/example?a=1&b=2'))
        .respond(200, sampleResponse());

      httpClient.get('/example', sampleData())
        .then(expectSampleResponse, expectNoError);

      $httpBackend.flush();
    });
  });

  describe('httpClient.post', function() {
    it('should unwrap post response', function() {
      $httpBackend.expectPOST(apiUrl('/example'))
        .respond(200, sampleResponse());

      httpClient.post('/example')
        .then(expectSampleResponse, expectNoError);

      $httpBackend.flush();
    });

    it('should pass post params', function() {
      $httpBackend.expectPOST(apiUrl('/example'), sampleData())
        .respond(200, sampleResponse());

      httpClient.post('/example', sampleData())
        .then(expectSampleResponse, expectNoError);

      $httpBackend.flush();
    });
  });

  describe('file upload', function() {
    var $upload;
    beforeEach(inject(function(_$upload_) {
      $upload = _$upload_;
    }));

    it('should send upload request to valid address', function() {
      $httpBackend.expectPOST(apiUrl('/example'))
        .respond(200, sampleResponse());

      httpClient.uploadFile('/example', { file: 'aa' })
        .then(expectSampleResponse, expectNoError);

      $httpBackend.flush();
    });

    it('should upload file content', function() {
      spyOn($upload, 'upload').and.callFake(function() {
        var result = {};
        result.success = _.constant(result);
        result.error = _.constant(result);
        return result;
      });

      httpClient.uploadFile('/example', {file: 'aa'});

      expect($upload.upload).toHaveBeenCalledWith({
        url: apiUrl('/example'),
        file: 'aa'
      });
    });
  });

  describe('httpClient.delete', function() {
    it('should unwrap delete response', function() {
      $httpBackend.expectDELETE(apiUrl('/example'))
        .respond(200, sampleResponse());

      httpClient.delete('/example')
        .then(expectSampleResponse, expectNoError);

      $httpBackend.flush();
    });
  });

  function expectSampleResponse(response) { expect(response).toEqual(sampleResponse()); }
  function expectNoError(err) { expect(err).toBeUndefined(); }
  function sampleData() { return { a: 1, b: 2 }; }
  function sampleResponse() { return { value: 1}; }
});
