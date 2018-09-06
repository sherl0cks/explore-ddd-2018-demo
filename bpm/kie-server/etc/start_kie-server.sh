#!/usr/bin/env bash

# assume route is created
SERVER_HOST=$(oc get route kie-server --template="{{.spec.host}}")
SERVER_PATH=$(oc get route kie-server --template="{{.spec.path}}")
KIE_SERVER_LOCATION=http://$SERVER_HOST$SERVER_PATH
echo "KIE_SERVER_LOCATION: $KIE_SERVER_LOCATION"

# assume route is created and workbench is available
CONTROLLER_HOST=$(oc get route kie-workbench --template="{{.spec.host}}")
CONTROLLER_PATH=$(oc get route kie-workbench --template="{{.spec.path}}")
KIE_SERVER_CONTROLLER=http://kie-workbench:8080/jbpm-console/rest/controller #http://$CONTROLLER_HOST$CONTROLLER_PATH/rest/controller
KIE_MAVEN_REPO=http://kie-workbench:8080/jbpm-console/maven2 #http://$CONTROLLER_HOST$CONTROLLER_PATH/maven2
echo "KIE_SERVER_CONTROLLER: $KIE_SERVER_CONTROLLER"
echo "KIE_MAVEN_REPO: $KIE_MAVEN_REPO"

# Default arguments for running the KIE Execution server.
JBOSS_ARGUMENTS=" -b $JBOSS_BIND_ADDRESS -Dorg.kie.server.id=$KIE_SERVER_ID -Dorg.kie.server.location=$KIE_SERVER_LOCATION "

# Controller argument for the KIE Execution server, as we assume controller exists
JBOSS_ARGUMENTS="$JBOSS_ARGUMENTS -Dorg.kie.server.controller=$KIE_SERVER_CONTROLLER  -Dkie.maven.settings.custom=$JBOSS_HOME/../.m2/settings.xml"

# Start Wildfly with the given arguments.
echo "Running KIE Execution Server on JBoss Wildfly..."
exec ./standalone.sh $JBOSS_ARGUMENTS -c standalone-full-kie-server.xml
exit $?