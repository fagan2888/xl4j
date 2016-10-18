package com.mcleodmoores.xl4j.javacode;

import java.lang.reflect.Method;

import com.mcleodmoores.xl4j.typeconvert.TypeConverter;
import com.mcleodmoores.xl4j.values.XLObject;
import com.mcleodmoores.xl4j.values.XLValue;

/**
 * A class holding the converters required to convert arguments into the appropriate types and convert the result. 
 */
public class ObjectResultMethodInvoker extends AbstractMethodInvoker {
  private TypeConverter _objectXlObjectConverter;
  
  /**
   * Constructor.
   * @param method  the method to call.
   * @param argumentConverters  the converters required to call the method
   * @param returnConverter  the converter required to convert he result back to an Excel type
   * @param objectXlObjectConverter  a converter to convert the object into an object if necessary
   */
  public ObjectResultMethodInvoker(final Method method, final TypeConverter[] argumentConverters, 
                       final TypeConverter returnConverter, final TypeConverter objectXlObjectConverter) {
    super(method, argumentConverters, returnConverter);
    _objectXlObjectConverter = objectXlObjectConverter;
  }

  @Override
  protected XLValue convertResult(final Object object, final TypeConverter returnConverter) {
    if (object instanceof XLObject) { 
      // if multiple layers of method invoker, don't put already encoded XLObjects on heap.
      // this happens in case of JMethod/JConstruct etc.
      return (XLValue) object;
    } else {
      return (XLValue) _objectXlObjectConverter.toXLValue(null, object);
    }
  }

}