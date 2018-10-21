package submit;

class TestFaintness {
    /**
     * In this method all variables are faint because the final value is never used.
     * Sample out is at src/test/Faintness.out
     */
    void test1() {
        int x = 2;
        int y = x + 2;
        int z = x + y;
        return;
    }

    /**
     * Write your test cases here. Create as many methods as you want.
     * Run the test from root dir using
     * ./run.sh flow.Flow submit.MySolver submit.Faintness submit.TestFaintness
     */
    /*
      void test2() {
      }
      ...
    */

    /**
     * These test cases are designed according to the following principles:
     * (1) there is no MOVE/BINARY quad
     * 		(a) all the variables are faint
     * 		(b) some of the variables are faint
     * 		(c) none of the variables is faint
     * (2) there is some MOVE/BINARY quad
     * 		(a) all the defined variable in this quad are faint
     * 		(b) none of the defined variable in this quad is faint
     * (3) some additional case concerning branch
     * Nevertheless, the actual situation is much more complex due to the loss of original code structure after the java compiler
     */

    /**
     * no MOVE/BINARY: all the variable are faint
     */
    void test2() {
	    int a = 1;
	    int b = 2;
	    int c = 3;
	    int d = 4;
	    int e = 5;
    }

    /**
     * no MOVE/BINARY: some of the variable are faint, in this case, all variables except a are faint since only the value of a is finally used
     */
    int test3() {
	    int a = (int)Math.sin(100);
	    int b = 2;
	    return a;
    }

    /**
     * no MOVE/BINARY(actually with): none of the variables is faint since they all contribute to the final output value
     */
    int test4() {
	    int a = (int)Math.sin(1);
	    int b = (int)Math.sin(2);
	    return a + b;
    }

    /**
     * with MOVE/BINARY: all the defined variables in the MOVE/BINARY quad are fiant
     * fant-variables: a, b, c 
     * reason: these variables are never truly used, a and b are only used to calculate another dead variable c
     */
    void test5() {
	    int a = 1;
	    int b = 2;
	    int c = a + b;
    }

    /**
     * with MOVE/BINARY: none of the defined variable in the MOVE/BINARY quad are faint    
     * faint-variables: none
     * reason: since c is not faint, and as a result, these variables used to calculate c are not faint
     */
    int test6() {
	    int a = 1;
	    int b = 2;
	    int c = a + b;
	    return c;
    }

    /** 
     * some additional case concerning branch
     * faint-variables: none
     * reason: since a may be used in one of the if case
     */
    int test7() {
	    int a = (int)Math.sin(10);
	    if (a > 10) {
		    return a;
	    } else {
		    return 0;
	    }
    }
}
