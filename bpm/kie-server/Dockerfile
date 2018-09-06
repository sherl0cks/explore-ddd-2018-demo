FROM jboss/kie-server-showcase:7.10.0.Final

ADD etc/settings.xml $JBOSS_HOME/../.m2/settings.xml
RUN mkdir $JBOSS_HOME/../.m2/repository && \
    chown jboss:root $JBOSS_HOME/../.m2/repository