function createModalInstance() {
  return {
    close: jasmine.createSpy('modal close'),
    dismiss: jasmine.createSpy('modal dismiss')
  }
}
