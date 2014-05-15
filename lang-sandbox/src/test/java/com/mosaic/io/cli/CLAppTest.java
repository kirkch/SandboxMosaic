package com.mosaic.io.cli;

import com.mosaic.lang.system.DebugSystem;
import com.mosaic.lang.system.SystemX;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class CLAppTest {

    private DebugSystem system = new DebugSystem();


    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsZeroWhenRun_invoke_expectZero() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp() );

        system.assertNoLogMessages();
        system.assertStandardOutEquals();
    }

    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsOneWhenRun_invoke_expectOne() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 1;
            }
        };

        assertEquals( 1, app.runApp() );

        system.assertNoLogMessages();
        system.assertStandardOutEquals();
    }

    @Test
    public void givenAppWithNoFlagsNoArgsNoDescriptionAndReturnsOneWhenRun_requestHelp_expectDefaultHelp() {
        CLApp2 app = new CLApp2(system) {
            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName(),
            "",
            "optional flags:",
            "",
            "    --help display this usage information",
            ""
        );
    }

    @Test
    public void givenAppWithDescriptionOnly_requestHelp_expectDescriptionInHelp() {
        CLApp2 app = new CLApp2(system) {
            {
                setDescription( "This is a test app.  Enjoy." );
            }

            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName(),
            "",
            "This is a test app.  Enjoy.",
            "",
            "optional flags:",
            "",
            "    --help display this usage information",
            ""
        );
    }

    @Test
    public void givenAppWithTwoMandatoryStringArgs_requestHelp_expectDescriptionInHelp() {
        CLApp2 app = new CLApp2(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgument( "destination", "where to copy the file to" );
            }

            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName()+ " source destination",
            "",
            "This is a test app.  Enjoy.",
            "",
            "    source      - the file to be copied",
            "    destination - where to copy the file to",
            "",
            "optional flags:",
            "",
            "    --help display this usage information",
            ""
        );

        system.assertStandardErrorEquals();
    }

    @Test
    public void checkThatDescriptionIsWrappedAt80Characters() {
        CLApp2 app = new CLApp2(system) {
            {
                setDescription( "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" );
            }

            protected int _run() {
                return 1;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName(),
            "",
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890",
            "12345678901234567890",
            "",
            "optional flags:",
            "",
            "    --help display this usage information",
            ""
        );
    }

    @Test
    public void checkThatArgumentNameAndDescriptionsAreTruncatedAt80Characters() {
        CLApp2 app = new CLApp2(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" );
                this.destination = registerArgument( "destination", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" );
            }

            protected int _run() {
                return 10;
            }
        };

        assertEquals( 0, app.runApp("--help") );

        system.assertStandardOutEquals(
            "Usage: "+app.getClass().getName()+ " source destination",
            "",
            "This is a test app.  Enjoy.",
            "",
            "    source      - 12345678901234567890123456789012345678901234567890123456789012",
            "    destination - 12345678901234567890123456789012345678901234567890123456789012",
            "",
            "optional flags:",
            "",
            "    --help display this usage information",
            ""
        );
    }



    @Test
    public void givenAppWithTwoMandatoryStringArgs_invokeWithOneMissingArg_expectErrorAndUsageDescription() {
        CLApp2 app = new CLApp2(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgument( "destination", "where to copy the file to" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 1, app.runApp("abc") );

        system.assertStandardErrorEquals(
            "Missing required argument 'destination', for more information invoke with --help."
        );

        system.assertStandardOutEquals();
        system.assertNoLogMessages();
    }

    @Test
    public void givenAppWithTwoMandatoryStringArgs_invokeWithBothMissing_expectErrorAndUsageDescription() {
        CLApp2 app = new CLApp2(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgument( "destination", "where to copy the file to" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 1, app.runApp() );

        system.assertStandardErrorEquals(
            "Missing required argument 'source', for more information invoke with --help."
        );

        system.assertStandardOutEquals();
        system.assertNoLogMessages();
    }

    @Test
    public void givenAppWithTwoMandatoryStringArgs_invokeWithBothArgs_expectItToRun() {
        CLApp2 app = new CLApp2(system) {
            public CLArgument<String> source;
            public CLArgument<String> destination;

            {
                setDescription( "This is a test app.  Enjoy." );

                this.source      = registerArgument( "source", "the file to be copied" );
                this.destination = registerArgument( "destination", "where to copy the file to" );
            }

            protected int _run() {
                return 0;
            }
        };

        assertEquals( 0, app.runApp("a","b") );

        system.assertStandardErrorEquals();
        system.assertStandardOutEquals();
        system.assertNoLogMessages();
    }





//    givenAppWithOneMandatoryOneOptionalArg_invokeWithBothArgs_expectItToRun
//    givenAppWithOneMandatoryOneOptionalArg_invokeWithMandatoryArgOnly_expectItToRun
//    givenAppWithOneMandatoryOneOptionalArg_invokeWithOutMandatoryArg_expectUsageDescription
//    givenAppWithOneMandatoryOneOptionalArg_requestHelp_expectArgDescriptionsInTheHelp

//    tryToCreateAppWithOptionalThenMandatoryArgs_expectErrorAsMandatoryMustGoBeforeOptional

//    givenTypedArgument_invokeWithArgThatWillFailParsing_expectError
//    givenTypedArgument_invokeWithArgThatWillPassParsing_expectItToRun


//    todo arg types (string, file, boolean, dir, files, ...)
//    todo flags and flag types  (boolean, string, int, enum, size, date, time, ...)



// cmd flags args

// -v --verbose
// --logLevel=debug -d --debug
// --logLevel=audit -a --audit
// --logLevel=info -i --info

    private static class AppWithFlags extends CLApp2 {

//        private final CLArgument<LogLevelEnum> logLevelFlag;   // .get()   .setDefaultValue(v)
//
//        private final CLArgument<FileX>        sourceFile;


        public AppWithFlags( SystemX system ) {
            super( system );

//            setAppDescription( " " );
//
//            this.isVerboseCLF = registerBooleanFlag( "verbose", "v", "description" );
//            this.logLevelCLF  = registerEnumFlag( "logLevel", "description", LogLevelEnum.class,
//                    new CLEnum(Debug, "debug", "", "description"),
//                    new CLEnum(Info, "info|verbose", "v", "description"),
//                    new CLEnum(Audit, "audit", "", "description"),
//                    new CLEnum(Warn, "warn", "", "description"),
//                    new CLEnum(Error, "error", "", "description"),
//                    new CLEnum(Fatal, "fatal", "", "description")
//                ).withDefaultOf(Audit);

//            this.sourceFile   = registerArgument[T]( "source", "desc", f(s):T )
        }


        protected int _run() {
            return 0;
        }
    }

}
