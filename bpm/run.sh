#!/usr/bin/env bash

if [ -z "$1" ]; then
    echo "Please supply an OpenShift project name to run.sh"
    exit 1
fi

oc new-project $1

oc new-app --name kie-workbench jboss/jbpm-workbench-showcase:latest
oc expose svc kie-workbench --path=/jbpm-console --port=8080


oc new-app --name kie-server https://github.com/sherl0cks/explore-ddd-2018-demo --context-dir bpm/kie-server \
-e KIE_SERVER_CONTROLLER=http://kie-workbench:8080/jbpm-console/rest/controller \
-e KIE_MAVEN_REPO=http://kie-workbench:8080/jbpm-console/maven2 \
-e JAVA_OPTS='-server -Xms256m -Xmx1024m -Djava.net.preferIPv4Stack=true -Dkie.maven.settings.custom=$JBOSS_HOME/../.m2/settings.xml' \
-e KIE_SERVER_ID=openshift-kie-server

oc expose svc kie-server --path=/kie-server/services/rest/server

SERVER_HOST=$(oc get route kie-server --template="{{.spec.host}}")
SERVER_PATH=$(oc get route kie-server --template="{{.spec.path}}")
KIE_SERVER_LOCATION=http://$SERVER_HOST$SERVER_PATH
echo "KIE_SERVER_LOCATION: $KIE_SERVER_LOCATION"

oc set env dc/kie-server -e KIE_SERVER_LOCATION=$KIE_SERVER_LOCATION