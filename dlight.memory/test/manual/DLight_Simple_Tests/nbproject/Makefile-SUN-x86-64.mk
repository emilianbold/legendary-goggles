#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=cc
CCC=CC
CXX=CC
FC=f95
AS=as

# Macros
CND_PLATFORM=SunStudio-Solaris-x86

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/SUN-x86-64/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/deadlock.o \
	${OBJECTDIR}/test_alloc.o \
	${OBJECTDIR}/test_write.o \
	${OBJECTDIR}/profiler_tests_main.o \
	${OBJECTDIR}/worker.o \
	${OBJECTDIR}/pi.o \
	${OBJECTDIR}/test_dl.o \
	${OBJECTDIR}/test_sync.o

# C Compiler Flags
CFLAGS=-xarch=generic64

# CC Compiler Flags
CCFLAGS=-xarch=generic64
CXXFLAGS=-xarch=generic64

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-SUN-x86-64.mk dist/SUN-x86-64/SunStudio-Solaris-x86/dlight_simple_tests

dist/SUN-x86-64/SunStudio-Solaris-x86/dlight_simple_tests: ${OBJECTFILES}
	${MKDIR} -p dist/SUN-x86-64/SunStudio-Solaris-x86
	${LINK.cc} -lmalloc -o dist/SUN-x86-64/${CND_PLATFORM}/dlight_simple_tests ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/deadlock.o: deadlock.cpp 
	${MKDIR} -p ${OBJECTDIR}
	$(COMPILE.cc) -fast -o ${OBJECTDIR}/deadlock.o deadlock.cpp

${OBJECTDIR}/test_alloc.o: test_alloc.c 
	${MKDIR} -p ${OBJECTDIR}
	$(COMPILE.c) -fast -o ${OBJECTDIR}/test_alloc.o test_alloc.c

${OBJECTDIR}/test_write.o: test_write.cpp 
	${MKDIR} -p ${OBJECTDIR}
	$(COMPILE.cc) -fast -o ${OBJECTDIR}/test_write.o test_write.cpp

${OBJECTDIR}/profiler_tests_main.o: profiler_tests_main.c 
	${MKDIR} -p ${OBJECTDIR}
	$(COMPILE.c) -fast -o ${OBJECTDIR}/profiler_tests_main.o profiler_tests_main.c

${OBJECTDIR}/worker.o: worker.cpp 
	${MKDIR} -p ${OBJECTDIR}
	$(COMPILE.cc) -fast -o ${OBJECTDIR}/worker.o worker.cpp

${OBJECTDIR}/pi.o: pi.c 
	${MKDIR} -p ${OBJECTDIR}
	$(COMPILE.c) -fast -o ${OBJECTDIR}/pi.o pi.c

${OBJECTDIR}/test_dl.o: test_dl.c 
	${MKDIR} -p ${OBJECTDIR}
	$(COMPILE.c) -fast -o ${OBJECTDIR}/test_dl.o test_dl.c

${OBJECTDIR}/test_sync.o: test_sync.cpp 
	${MKDIR} -p ${OBJECTDIR}
	$(COMPILE.cc) -fast -o ${OBJECTDIR}/test_sync.o test_sync.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/SUN-x86-64
	${RM} dist/SUN-x86-64/SunStudio-Solaris-x86/dlight_simple_tests
	${CCADMIN} -clean

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
