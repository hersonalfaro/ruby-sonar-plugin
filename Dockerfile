FROM sonarqube:6.4


 # Install packages for building ruby
 RUN apt-get update
 RUN apt-get install -y build-essential curl git
 RUN apt-get install -y zlib1g-dev libssl-dev libreadline-dev libyaml-dev libxml2-dev libxslt-dev
 RUN apt-get clean

RUN wget -qP /opt/sonarqube/extensions/plugins https://github.com/hersonalfaro/ruby-sonar-plugin/releases/download/6.4-rc/sonar-ruby-plugin.jar 
