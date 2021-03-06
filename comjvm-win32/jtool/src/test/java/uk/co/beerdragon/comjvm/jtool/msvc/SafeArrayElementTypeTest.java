/*
 * COM Java wrapper 
 *
 * Copyright 2014 by Andrew Ian William Griffin <griffin@beerdragon.co.uk>.
 * Released under the GNU General Public License.
 */

package uk.co.beerdragon.comjvm.jtool.msvc;

import org.testng.Assert;
import org.testng.annotations.Test;

import uk.co.beerdragon.comjvm.jtool.JavaType;

/**
 * Tests the {@link SafeArrayElementType} class.
 */
@Test
public class SafeArrayElementTypeTest {

  /**
   * Tests the conversion to safe array elements.
   */
  @Test
  public void testToSafeArrayElementType () {
    final JavaType.Visitor<String> idl = new SafeArrayElementType ();
    Assert.assertEquals (JavaType.BOOLEAN_TYPE.accept (idl), "BOOL");
    Assert.assertEquals (JavaType.CHAR_TYPE.accept (idl), "char");
    Assert.assertEquals (JavaType.BYTE_TYPE.accept (idl), "byte");
    Assert.assertEquals (JavaType.SHORT_TYPE.accept (idl), "short");
    Assert.assertEquals (JavaType.INT_TYPE.accept (idl), "long");
    Assert.assertEquals (JavaType.LONG_TYPE.accept (idl), "hyper");
    Assert.assertEquals (JavaType.FLOAT_TYPE.accept (idl), "float");
    Assert.assertEquals (JavaType.DOUBLE_TYPE.accept (idl), "double");
    Assert.assertEquals (new JavaType.ObjectType ("java.lang.String").accept (idl), "VARIANT");
    Assert.assertEquals (new JavaType.ArrayType (JavaType.INT_TYPE).accept (idl), "VARIANT");
  }

}