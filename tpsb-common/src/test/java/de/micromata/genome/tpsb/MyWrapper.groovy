package de.micromata.genome.tpsb

class OtherWrapper extends MyFoo {
  Object target;
  Class<? extends Throwable> exptectedEx;
  OtherWrapper(Object target, Class<? extends Throwable> exptectedEx) {
    this.target = target;
    this.exptectedEx = exptectedEx;
  }
  def wrappExpectedEx(code) {
    boolean thrown = false;
    try {
      code();
      thrown = true;
      throw new AssertionFailedException("Missed Excected exception: " + exptectedEx.getName());
    } catch (Throwable ex) {
      if (thrown == true) {
        throw (AssertionFailedException)ex;
      }
      if (exptectedEx.isAssignableFrom(ex.getClass()) == true) {
        return getBuilder();
      }
      throw new AssertionFailedException("Wrong exception. expected: " + exptectedEx.getName() + "; got: " + ex.getClass().getName() + "; " + ex.getMessage(), ex);
    }
  }
  MyFoo foo(String arg) {
    return wrappExpectedEx( { target.foo(arg) });
  }
}
MyFoo foo = new MyFoo();
def w = new OtherWrapper(foo, IllegalArgumentException.class);
w.foo("asdf");