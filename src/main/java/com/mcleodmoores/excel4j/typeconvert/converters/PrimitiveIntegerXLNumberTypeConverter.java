package com.mcleodmoores.excel4j.typeconvert.converters;

<<<<<<< HEAD
import com.mcleodmoores.excel4j.typeconvert.AbstractScalarTypeConverter;
=======
import com.mcleodmoores.excel4j.typeconvert.AbstractTypeConverter;
import com.mcleodmoores.excel4j.util.ArgumentChecker;
>>>>>>> be5ef72d71080f3da434520e38f82a0ab8dd969f
import com.mcleodmoores.excel4j.values.XLNumber;
import com.mcleodmoores.excel4j.values.XLValue;

/**
 * Type converter to convert from integers to Excel Numbers and back again.
 */
public final class PrimitiveIntegerXLNumberTypeConverter extends AbstractScalarTypeConverter {
  /**
   * Default constructor.
   */
  public PrimitiveIntegerXLNumberTypeConverter() {
    super(Integer.TYPE, XLNumber.class);
  }

  @Override
  public XLValue toXLValue(final Class<? extends XLValue> expectedClass, final Object from) {
    ArgumentChecker.notNull(from, "from");
    return XLNumber.of((Integer) from);
  }

  @Override
  public Object toJavaObject(final Class<?> expectedClass, final XLValue from) {
    ArgumentChecker.notNull(from, "from");
    return (int) ((XLNumber) from).getValue();
  }
}
