package com.mcleodmoores.excel4j.javacode;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mcleodmoores.excel4j.typeconvert.AbstractTypeConverter;
import com.mcleodmoores.excel4j.typeconvert.TypeConverter;
import com.mcleodmoores.excel4j.util.Excel4JRuntimeException;
import com.mcleodmoores.excel4j.values.XLValue;

/**
 * A class holding the converters required to convert arguments into the appropriate types and convert the result.
 */
public abstract class AbstractMethodInvoker implements MethodInvoker {
  private static Logger s_logger = LoggerFactory.getLogger(AbstractMethodInvoker.class);
  private final Method _method;
  private final TypeConverter[] _argumentConverters;
  private final TypeConverter _returnConverter;

  /**
   * Constructor.
   * @param method  the method to call.
   * @param argumentConverters  the converters required to call the method
   * @param returnConverter  the converter required to convert he result back to an Excel type
   */
  public AbstractMethodInvoker(final Method method, final TypeConverter[] argumentConverters,
                       final TypeConverter returnConverter) {
    _method = method;
    _argumentConverters = argumentConverters;
    _returnConverter = returnConverter;
  }

  @Override
  public XLValue invoke(final Object object, final XLValue[] arguments) {
    // note that the seemingly obvious invariant of arguments.length == _argumentConverters.length is not
    // always true because of a VarArgs might have no arguments to it's converter may be surplus to
    // requirements.  For this reason we base the conversion on the length of arguments.

    final Object[] args = new Object[_argumentConverters.length];
    if (isVarArgs()) {
      if (arguments.length < _argumentConverters.length) { // var args is empty
        for (int i = 0; i < arguments.length; i++) {
          if (arguments[i].getClass().isArray()) {
            //AbstractArrayTypeConverter arrayTypeConverter = (AbstractArrayTypeConverter) _argumentConverters[i];
            //Object[] subArray = arrayTypeConverter.getExcelToJavaTypeMapping().getExcelClass().newInstance();
            args[i] = arguments[i];
          } else {
            final AbstractTypeConverter scalarTypeConverter = (AbstractTypeConverter) _argumentConverters[i];
            args[i] = scalarTypeConverter.toJavaObject(null, arguments[i]);
          }

        }
        args[_argumentConverters.length - 1] = createVarArgsArray(_method, 0); // empty varargs to pass on
      } else { // args args non-empty
        for (int i = 0; i < _argumentConverters.length - 1; i++) {  // last arg converter used for var args.
          if (arguments[i].getClass().isArray()) {
            args[i] = arguments[i];
          } else {
            final AbstractTypeConverter scalarTypeConverter = (AbstractTypeConverter) _argumentConverters[i];
            args[i] = scalarTypeConverter.toJavaObject(null, arguments[i]);
          }
        }
        final Object[] varArgs = createVarArgsArray(_method, arguments.length - (_argumentConverters.length - 1)); // so if converters == arguments, we have 1.
        for (int i = 0; i < varArgs.length; i++) {
//          if (arguments[i + (_argumentConverters.length - 1)].getClass().isArray()) { // should test for an XLArray?
//            varArgs[i] = args[i];
//          } else {
            //AbstractTypeConverter scalarTypeConverter = (AbstractTypeConverter) _argumentConverters[_argumentConverters.length - 1];
            //varArgs[i] = scalarTypeConverter.toJavaObject(null, arguments[i + (_argumentConverters.length - 1)]);
            varArgs[i] = arguments[i + (_argumentConverters.length - 1)]; // passthrough.
//          }
        }
        args[_argumentConverters.length - 1] = varArgs; // non-empty varargs to pass on
      }
    } else {
      for (int i = 0; i < arguments.length; i++) {
        if (arguments[i].getClass().isArray()) {
          args[i] = arguments[i];
        } else {
          final AbstractTypeConverter scalarTypeConverter = (AbstractTypeConverter) _argumentConverters[i];
          args[i] = scalarTypeConverter.toJavaObject(null, arguments[i]);
        }
      }
    }
    try {
      s_logger.info("invoking " + object + " with " + Arrays.toString(args));
      final Object result = _method.invoke(object, args);
      return convertResult(result, _returnConverter);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
      throw new Excel4JRuntimeException("Error invoking method", e);
    }
  }

  private Object[] createVarArgsArray(final Method method, final int size) {
    final Class<?>[] parameterTypes = method.getParameterTypes();
    return (Object[]) Array.newInstance(parameterTypes[parameterTypes.length - 1].getComponentType(), size);
  }

  /**
   * Processes the result object into the object returned by excel.  Could be an object or an Excel type.
   * @param object  the result object to process
   * @param returnConverter  the simplifying return converter
   * @return an XLValue type
   */
  protected abstract XLValue convertResult(final Object object, final TypeConverter returnConverter);

  @Override
  public Class<?>[] getExcelParameterTypes() {
    final Class<?>[] parameterTypes = new Class[_argumentConverters.length];
    final int i = 0;
    for (final TypeConverter typeConverter : _argumentConverters) {
      parameterTypes[i] = typeConverter.getJavaToExcelTypeMapping().getExcelClass();
    }
    return parameterTypes;
  }

  @Override
  public Class<?> getExcelReturnType() {
    return _returnConverter.getJavaToExcelTypeMapping().getExcelClass();
  }

  @Override
  public boolean isStatic() {
    return Modifier.isStatic(_method.getModifiers());
  }

  @Override
  public boolean isVarArgs() {
    return _method.isVarArgs();
  }

  @Override
  public String getMethodName() {
    return _method.getName();
  }

  @Override
  public Class<?> getMethodDeclaringClass() {
    return _method.getDeclaringClass();
  }
}
