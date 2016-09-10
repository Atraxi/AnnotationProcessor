package atraxi.annotation.annotations;

/**
 * Created by Atraxi on 8/09/2016.
 */
public @interface Serialize
{
    Visibility value();

    public enum Visibility
    {
        ALL, PARTIAL, MINIMAL;
    }
}