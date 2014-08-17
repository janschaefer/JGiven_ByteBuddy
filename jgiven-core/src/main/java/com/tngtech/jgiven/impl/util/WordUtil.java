package com.tngtech.jgiven.impl.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

public final class WordUtil {

    private WordUtil() {}

    /**
     * Returns the given text with the first letter in upper case.
     *
     * <h2>Examples:</h2>
     * <pre>
     * capitalize("hi") == "Hi"
     * capitalize("Hi") == "Hi"
     * capitalize("hi there") == "hi there"
     * capitalize("") == ""
     * capitalize(null) == null
     * </pre>
     * @param text the text to capitalize
     * @return text with the first letter in upper case
     */
    public static String capitalize( String text ) {
        if( text == null || text.isEmpty() ) {
            return text;
        }
        return text.substring( 0, 1 ).toUpperCase().concat( text.substring( 1, text.length() ) );
    }

    public static String lowerCaseFirstChar( String text ) {
        if( text == null || text.isEmpty() ) {
            return text;
        }
        return text.substring( 0, 1 ).toLowerCase().concat( text.substring( 1, text.length() ) );
    }

    public static String camelCase( String string ) {
        return lowerCaseFirstChar( Joiner.on( "" )
            .join( FluentIterable.from( Splitter.on( '_' ).split( string ) ).transform( capitalize() ) ) );
    }

    private static Function<String, String> capitalize() {
        return new Function<String, String>() {
            @Override
            public String apply( String arg ) {
                return capitalize( arg );
            }
        };
    }

}
