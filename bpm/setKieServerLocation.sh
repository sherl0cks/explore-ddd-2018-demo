#!/usr/bin/env bash

SERVER_HOST=$(oc get route kie-server --template="{{.spec.host}}")
SERVER_PATH=$(oc get route kie-server --template="{{.spec.path}}")
KIE_SERVER_LOCATION=http://$SERVER_HOST$SERVER_PATH
echo "KIE_SERVER_LOCATION: $KIE_SERVER_LOCATION"

oc set env dc/kie-server -e KIE_SERVER_LOCATION=$KIE_SERVER_LOCATION