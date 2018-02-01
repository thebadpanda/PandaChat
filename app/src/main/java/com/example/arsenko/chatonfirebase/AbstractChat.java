package com.example.arsenko.chatonfirebase;

public abstract class AbstractChat {

    public abstract String getMessage();

    public abstract String getName();

    public abstract String getUid();

//    public abstract long getTime();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
}
