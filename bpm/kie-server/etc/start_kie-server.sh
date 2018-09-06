#!/usr/bin/env bash

# assume route is created
HOST=$(oc get route kie-server --template="{{.spec.host}}")
PATH=$(oc get route kie-server --template="{{.spec.path}}")
KIE_SERVER_LOCATION=http://$HOST$PATH
echo "KIE_SERVER_LOCATION: $KIE_SERVER_LOCATION"

# assume route is created and workbench is available
HOST=$(oc get route kie-workbench --template="{{.spec.host}}")
PATH=$(oc get route kie-workbench --template="{{.spec.path}}")
KIE_SERVER_CONTROLLER=http://$HOST$PATH/rest/controller
KIE_MAVEN_REPO=http://$HOST$PATH/maven2
echo "KIE_SERVER_CONTROLLER: $KIE_SERVER_CONTROLLER"
echo "KIE_MAVEN_REPO: $KIE_MAVEN_REPO"

# Default arguments for running the KIE Execution server.
JBOSS_ARGUMENTS=" -b $JBOSS_BIND_ADDRESS -Dorg.kie.server.id=$KIE_SERVER_ID -Dorg.kie.server.user=$KIE_SERVER_USER -Dorg.kie.server.pwd=$KIE_SERVER_PWD -Dorg.kie.server.location=$KIE_SERVER_LOCATION "

# Controller argument for the KIE Execution server, as we assume controller exists
JBOSS_ARGUMENTS="$JBOSS_ARGUMENTS -Dorg.kie.server.controller=$KIE_SERVER_CONTROLLER -Dorg.kie.server.controller.user=$KIE_SERVER_CONTROLLER_USER -Dorg.kie.server.controller.pwd=$KIE_SERVER_CONTROLLER_PWD -Dkie.maven.settings.custom=$JBOSS_HOME/../.m2/settings.xml"

# Start Wildfly with the given arguments.
echo "Running KIE Execution Server on JBoss Wildfly..."
exec ./standalone.sh $JBOSS_ARGUMENTS -c standalone-full-kie-server.xml
exit $?