package cz.martinbrom.slimybees.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This class contains useful functions for array manipulation.
 */
@ParametersAreNonnullByDefault
public class ArrayUtils {

    // prevent instantiation
    private ArrayUtils() {}

    /**
     * Concatenates two arrays (or an array and arbitrarily many elements) of one type
     * into one array of that type. Elements are "added" to the end of the first array.
     *
     * @param a First array
     * @param b Elements to be added
     * @param <T> Type of both the array and the elements
     * @return Array of the same type with all of the elements combined.
     */
    @Nonnull
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> T[] concat(T[] a, T... b) {
        return Stream.concat(Arrays.stream(a), Stream.of(b)).toArray(
                size -> (T[]) Array.newInstance(a.getClass().getComponentType(), size));
    }

    /**
     * Checks whether given array contains given element.
     *
     * @param array Array to be checked (haystack)
     * @param item Item to find (needle)
     * @param <T> Type of both the array and the elements
     * @return Whether given array contains given element
     */
    public static <T> boolean contains(T[] array, T item) {
        return Arrays.asList(array).contains(item);
    }

    /**
     * Performs an in-place shuffle of the array by repeatedly swapping pairs of elements.
     *
     * @param array Array to be shuffled
     * @param <T> Type of the array elements
     */
    public static <T> void shuffle(T[] array) {
        Random random = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);

            // swap
            T a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

}
