#!/usr/bin/env bash
set -ex

if [ -z "$1" ]; then
    echo "Please supply an OpenShift project name to run.sh"
    exit 1
fi

oc new-project $1

oc new-app --name kie-workbench jboss/jbpm-workbench-showcase:latest \
    -e JAVA_OPTS="-server -Xms256m -Xmx1024m -Djava.net.preferIPv4Stack=true -Dorg.uberfire.nio.git.ssh.host=0.0.0.0 -Dorg.uberfire.nio.git.daemon.host=0.0.0.0 -Dorg.uberfire.nio.git.dir=/opt/jboss/wildfly/git"
oc expose svc kie-workbench --path=/jbpm-console --port=8080
oc patch dc/kie-workbench --patch '{"spec":{"strategy":{"type":"Recreate"}}}'
oc volume dc/kie-workbench --add -m /opt/jboss/wildfly/git --claim-size=1G --name=kie-workbench

oc new-build --name kie-server-base https://github.com/sherl0cks/explore-ddd-2018-demo --context-dir bpm/kie-server
oc new-build --name kie-server --binary=true --strategy=source -i kie-server-base

oc new-app kie-server \
    -e KIE_SERVER_CONTROLLER=http://kie-workbench:8080/jbpm-console/rest/controller \
    -e KIE_MAVEN_REPO=http://kie-workbench:8080/jbpm-console/maven2 \
    -e JAVA_OPTS='-server -Xms256m -Xmx1024m -Djava.net.preferIPv4Stack=true -Dkie.maven.settings.custom=$JBOSS_HOME/../.m2/settings.xml' \
    -e KIE_SERVER_ID=openshift-kie-server

oc expose svc kie-server --path=/kie-server/services/rest/server

source setKieServerLocation.sh