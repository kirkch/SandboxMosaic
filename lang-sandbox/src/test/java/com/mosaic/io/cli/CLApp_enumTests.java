package com.mosaic.io.cli;

import com.mosaic.lang.system.SystemX;


/**
 *
 */
public class CLApp_enumTests {



// givenEnumFlag_requestHelp_expectFlagAndValuesToBeDocumented
// givenEnumFlag_invokeAppWithoutSpecifyingFlag_expectDefaultValueToBeUsed
// givenEnumFlag_invokeAppWithFlag_expectSuppliedValueToBeUsed
// givenEnumFlag_invokeAppWithUnknownEnumValue_expectError








// cmd OPTIONS args

// -v --verbose
// --logLevel=debug -d --debug
// --logLevel=audit -a --audit
// --logLevel=info -i --info

    private static class AppWithOPTIONS extends CLApp2 {

//        private final CLArgument<LogLevelEnum> logLevelFlag;   // .get()   .setDefaultValue(v)
//
//        private final CLArgument<FileX>        sourceFile;


        public AppWithOPTIONS( SystemX system ) {
            super( system );

//            setAppDescription( " " );
//
//            this.isVerboseCLF = registerFlag( "verbose", "v", "description" );
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
