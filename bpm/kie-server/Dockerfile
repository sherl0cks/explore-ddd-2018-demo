FROM jboss/kie-server-showcase:7.10.0.Final


LABEL io.openshift.s2i.scripts-url=image:///usr/libexec/s2i

####### KIE Server CUSTOM CONFIGURATION ############
ADD etc/settings.xml $JBOSS_HOME/../.m2/settings.xml


# Make sure maven repo is writable in k8s
# Move over the run script from the base image to the s2i dir, since base does not use s2i and this image does
USER root
RUN yum install git -y ; \ 
    mkdir $JBOSS_HOME/../.m2/repository && \
    chown jboss:root $JBOSS_HOME/../.m2/repository && \
    chmod 774 $JBOSS_HOME/../.m2/repository && \
    cp $JBOSS_HOME/bin/start_kie-server.sh /usr/libexec/s2i && \
    mv /usr/libexec/s2i/start_kie-server.sh /usr/libexec/s2i/run

COPY ./s2i/bin/ /usr/libexec/s2i

USER 1001