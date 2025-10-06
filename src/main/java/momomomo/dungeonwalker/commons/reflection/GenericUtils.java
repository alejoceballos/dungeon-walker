package momomomo.dungeonwalker.commons.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;

public class GenericUtils {

    public static Optional<Type> getGenericTypeArgument(final Object object) {
        return Optional.of(getGenericInterfaces(object))
                .map(toFirstParameterizedType())
                .map(toFirstTypeArgument());
    }

    private static Type[] getGenericInterfaces(Object object) {
        return object.getClass().getGenericInterfaces();
    }

    private static Function<ParameterizedType, Type> toFirstTypeArgument() {
        return parameterizedType -> parameterizedType.getActualTypeArguments()[0];
    }

    private static Function<Type[], ParameterizedType> toFirstParameterizedType() {
        return genericInterfaces -> (ParameterizedType) genericInterfaces[0];
    }


}
