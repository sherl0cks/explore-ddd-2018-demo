# explore-ddd-2018-demo
http://exploreddd.com/speakers/justin-holmes.html

oc policy add-role-to-user view -z default
oc new-app --name kie-workbench jboss/jbpm-workbench-showcase:latest
oc new-app --name kie-server jboss/kie-server-showcase:latest \
-e KIE_SERVER_CONTROLLER=http://kie-workbench:8080/jbpm-console/rest/controller \
-e KIE_MAVEN_REPO=http://kie-workbench:8080/jbpm-console/maven2 \
-e KIE_SERVER_LOCATION=http://kie-server:8080/kie-server/services/rest/server
oc expose svc kie-workbench --path=/jbpm-console --port=8080

