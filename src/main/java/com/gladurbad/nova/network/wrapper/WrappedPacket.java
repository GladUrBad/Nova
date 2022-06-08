package com.gladurbad.nova.network.wrapper;

import net.minecraft.server.v1_8_R3.Packet;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;


public class WrappedPacket {
    private final long time = System.currentTimeMillis();
    private final Map<String, Field> fields = new WeakHashMap<>();
    private final Packet<?> instance;

    public WrappedPacket(Packet<?> instance, Class<? extends Packet<?>> klass) {
        this.instance = instance;

        /*
         * But glad!! Reflection is 200% slower than direct access, and 100% slower than packet data serialization!
         * Suck my dick for I do not care.
         */
        for (Field field : klass.getDeclaredFields()) {
            field.setAccessible(true);
            fields.put(field.getName(), field);
        }
    }

    public <T> T getField(String name) {
        try {
            return (T) fields.get(name).get(instance);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public long getTime() {
        return time;
    }
}
