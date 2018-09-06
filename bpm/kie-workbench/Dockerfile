FROM jboss/jbpm-workbench-showcase:7.10.0.Final

## Make sure maven repo is writable in k8s
USER root
RUN yum install git -y ; \ 
    mkdir $JBOSS_HOME/git && \
    chown jboss:root $JBOSS_HOME/git && \
    chmod 774 $JBOSS_HOME/git

USER 1001