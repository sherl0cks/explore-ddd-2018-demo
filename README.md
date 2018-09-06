# explore-ddd-2018-demo
http://exploreddd.com/speakers/justin-holmes.html


oc new-app --name kie-workbench jboss/jbpm-workbench-showcase:latest
oc expose svc kie-workbench --path=/jbpm-console --port=8080

oc new-build https://github.com/sherl0cks/explore-ddd-2018-demo/ --name kie-server --context-dir bpm/kie-server

oc new-app --name kie-server --allow-missing-imagestream-tags=true kie-server:latest \
-e KIE_SERVER_CONTROLLER=http://kie-workbench:8080/jbpm-console/rest/controller \
-e KIE_MAVEN_REPO=http://kie-workbench:8080/jbpm-console/maven2 \
-e KIE_SERVER_LOCATION=http://kie-server-holmes-test.apps.s10.core.rht-labs.com/kie-server/services/rest/server \
-e JAVA_OPTS='-server -Xms256m -Xmx1024m -Djava.net.preferIPv4Stack=true -Dkie.maven.settings.custom=$JBOSS_HOME/../.m2/settings.xml' \
-e KIE_SERVER_ID=openshift-kie-server
oc expose svc kie-server --path=/kie-server/services/rest/server

