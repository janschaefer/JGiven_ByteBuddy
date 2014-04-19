package com.tngtech.jgiven.report.text;

import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD;
import static org.fusesource.jansi.Ansi.Color.MAGENTA;

import java.io.PrintStream;
import java.util.List;

import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;

public class PlainTextScenarioWriter extends PlainTextWriter {
    private static final String INDENT = "   ";

    private int maxFillWordLength;
    protected ScenarioModel currentScenarioModel;
    protected ScenarioCaseModel currentCaseModel;

    public PlainTextScenarioWriter( PrintStream stream, boolean withColor ) {
        super( stream, withColor );
    }

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        stream.print( "\n" + withColor( MAGENTA, INTENSITY_BOLD, " Scenario: " ) );
        println( Color.MAGENTA, scenarioModel.description + "\n" );
        currentScenarioModel = scenarioModel;
    }

    @Override
    public void visitEnd( ScenarioCaseModel scenarioCase ) {
        if( !scenarioCase.success ) {
            stream.println();
            stream.print( "FAILED: " + scenarioCase.errorMessage );
        }
        stream.println();
    }

    @Override
    public void visit( ScenarioCaseModel scenarioCase ) {
        if( currentScenarioModel.getScenarioCases().size() > 1 ) {
            printCaseLine( scenarioCase );
        }
        maxFillWordLength = new MaxFillWordLengthGetter().getLength( scenarioCase );
        currentCaseModel = scenarioCase;
    }

    protected void printCaseLine( ScenarioCaseModel scenarioCase ) {
        stream.print( "  Case " + scenarioCase.caseNr + ": " );
        List<String> arguments = scenarioCase.arguments;
        if( !arguments.isEmpty() ) {
            List<String> parameterNames = currentScenarioModel.parameterNames;
            for( int i = 0; i < arguments.size(); i++ ) {
                if( i < parameterNames.size() ) {
                    stream.print( parameterNames.get( i ) + " = " );
                }
                stream.print( arguments.get( i ) );
                if( i != arguments.size() - 1 ) {
                    stream.print( ", " );
                }
            }
        }
        stream.println();
    }

    static class MaxFillWordLengthGetter extends ReportModelVisitor {
        private int maxLength;

        public int getLength( ScenarioCaseModel scenarioCase ) {
            scenarioCase.accept( this );
            return maxLength;
        }

        @Override
        public void visit( StepModel stepModel ) {
            Word word = stepModel.words.get( 0 );
            if( word.isIntroWord ) {
                int length = word.value.length();
                if( length > maxLength ) {
                    maxLength = length;
                }
            }
        }
    }

    @Override
    public void visit( StepModel stepModel ) {
        String intro = "";
        List<Word> words = stepModel.words;
        if( words.get( 0 ).isIntroWord ) {
            intro = withColor( Color.BLUE, Attribute.INTENSITY_BOLD,
                INDENT + String.format( "%" + maxFillWordLength + "s ", WordUtil.capitalize( words.get( 0 ).value ) ) );
        } else {
            intro = INDENT + words.get( 0 ).value;
        }
        String rest = joinWords( words.subList( 1, words.size() ) );

        if( stepModel.notImplementedYet ) {
            rest = withColor( Color.BLACK, true, Attribute.INTENSITY_FAINT, rest + " (not implemented yet)" );
        } else if( stepModel.failed ) {
            rest = withColor( Color.RED, true, Attribute.INTENSITY_FAINT, rest );
            rest += withColor( Color.RED, true, Attribute.INTENSITY_BOLD, " (failed)" );
        }
        stream.println( intro + rest );
    }

    private String joinWords( List<Word> words ) {
        return Joiner.on( " " ).join( Iterables.transform( words, new Function<Word, String>() {
            @Override
            public String apply( Word input ) {
                return wordToString( input );
            }
        } ) );
    }

    protected String wordToString( Word word ) {
        if( word.isArg && !isInt( word ) ) {
            return "'" + word.value + "'";
        }
        return word.value;
    }

    private boolean isInt( Word word ) {
        try {
            Integer.valueOf( word.value );
            return true;
        } catch( NumberFormatException e ) {
            return false;
        }
    }

}
