FROM jboss/kie-server-showcase:7.10.0.Final

### OpenShift CLI
ENV OC_CLIENT_MIRROR https://mirror.openshift.com/pub/openshift-v3/clients/3.10.0-0.69.0/linux/oc.tar.gz

####### KIE Server CUSTOM CONFIGURATION ############
ADD etc/settings.xml $JBOSS_HOME/../.m2/settings.xml
# ADD etc/start_kie-server.sh $JBOSS_HOME/bin/start_kie-server.sh

## Make sure maven repo is writable in k8s
USER root
RUN mkdir $JBOSS_HOME/../.m2/repository && \
    chown jboss:root $JBOSS_HOME/../.m2/repository && \
    chmod 774 $JBOSS_HOME/../.m2/repository && \
    curl $OC_CLIENT_MIRROR | tar -C /usr/local/bin/ -xzf - ;

USER jboss