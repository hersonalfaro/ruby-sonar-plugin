FROM sonarqube:6.4
#WORKDIR /opt/sonar/

#COPY target/sonar-ruby-plugin-1.0.1.jar /opt/sonar/extensions/plugins/ 
RUN wget -qP /opt/sonarqube/extensions/plugins https://github.com/hersonalfaro/ruby-sonar-plugin/releases/download/6.4-rc/sonar-ruby-plugin-1.0.1.jar 
