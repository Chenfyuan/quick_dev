package cc.rc.framework.core.util;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @description:
 */
@UtilityClass
public class BeanUtils extends BeanUtil {
    public <T, R> List<T> copyList(Collection<R> objects, Class<T> tClass) {
        return copyList(objects, tClass, null, null);
    }

    public <T, R> List<T> copyList(Collection<R> objects, Class<T> tClass, String... ignoreProperties) {
        return copyList(objects, tClass, null, ignoreProperties);

    }

    public <T, R> List<T> copyList(Collection<R> objects, Class<T> tClass, Function<R, T> function, String... ignoreProperties) {
        if (CollectionUtil.isEmpty(objects)) {
            return new ArrayList<>();
        }
        List<T> target = new ArrayList<>(objects.size());
        for (R object : objects) {
            T t = copy(object, tClass, function, ignoreProperties);
            target.add(t);
        }

        return target;
    }

    public <T, R> T copy(R object, Class<T> tClass) {
        return copy(object, tClass, null, null);
    }

    public <T, R> T copy(R object, Class<T> tClass, String... ignoreProperties) {
        return copy(object, tClass, null,ignoreProperties);
    }

    public <T, R> T copy(R object, Class<T> tClass, Function<R, T> function,String... ignoreProperties) {
        if (ObjectUtil.isNull(object)) {
            return null;
        }
        T t = ReflectUtil.newInstanceIfPossible(tClass);;
        CopyOptions copyOptions = CopyOptions.create();
        if (ArrayUtil.isNotEmpty(ignoreProperties)) {
            copyOptions.setIgnoreProperties(ignoreProperties);
            copyProperties(object, t, ignoreProperties);
        } else {
            copyOptions.setIgnoreCase(false);
        }
        BeanCopier.create(object, t, copyOptions).copy();
        if (ObjectUtil.isNotNull(function)) {
            return function.apply(object, t);
        }

        return t;
    }

    private <T> T newInstance(Class<T> tClass) {
        T t = null;
        try {
            t = tClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }
    @FunctionalInterface
    public interface Function<R,T> {
        T apply(R r, T t);
    }

}

