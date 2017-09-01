FROM sonarqube:6.4


 # Install packages for building ruby
 RUN apt-get update
 RUN apt-get install -y build-essential curl git
 RUN apt-get install -y zlib1g-dev libssl-dev libreadline-dev libyaml-dev libxml2-dev libxslt-dev
 RUN apt-get clean

# Install rbenv and ruby-build
#RUN git clone https://github.com/sstephenson/rbenv.git /root/.rbenv
#RUN git clone https://github.com/sstephenson/ruby-build.git /root/.rbenv/plugins/ruby-buil
#RUN /root/.rbenv/plugins/ruby-build/install.sh
#ENV PATH /root/.rbenv/bin:$PATH
#RUN echo 'eval "$(rbenv init -)"' >> /etc/profile.d/rbenv.sh # or /etc/profile
#RUN echo 'eval "$(rbenv init -)"' >> .bashrc

# Install multiple versions of ruby
#ENV CONFIGURE_OPTS --disable-install-doc
#ADD ./versions.txt /root/versions.txt
#RUN apt-get install -y libssl-dev
#RUN apt-get install -y rake openssl openssl-devel byacc gem gcc gcc-c++ zlib-devel libxml2 libxml2-devel libxslt libxslt-devel patch gdal autoconf ImageMagick-devel postgresql-devel curl-devel httpd-devel apr-devel apr-util-devel readline-devel

#RUN git clone https://github.com/rbenv/rbenv.git ~/.rbenv
#RUN git clone https://github.com/rbenv/ruby-build.git ~/.rbenv/plugins/ruby-build

#RUN git clone https://github.com/rbenv/rbenv-vars.git ~/.rbenv/plugins/rbenv-vars
#RUN cd ~/.rbenv && src/configure && make -C src


#ENV PATH /root/.rbenv/bin:$PATH
#RUN echo 'eval "$(rbenv init -)"' >> .bashrc
#RUN /bin/bash 
#RUN rbenv install 2.3.0
#RUN rbenv global 2.3.0
#RUN gem install bundler 
#RUN xargs -L 1 rbenv install < /root/versions.txt

# Install Bundler for each version of ruby
#RUN echo 'gem: --no-rdoc --no-ri' >> /.gemrc
#RUN bash -l -c 'for v in $(cat /root/versions.txt); do rbenv global $v; gem install bundler; done' 







#RUN ruby -v
#RUN gem install simplecov-rcov 
#RUN gem install metric_fu


#COPY target/sonar-ruby-plugin-1.0.1.jar /opt/sonar/extensions/plugins/ 
#RUN wget -qP /opt/sonarqube/extensions/plugins https://github.com/hersonalfaro/ruby-sonar-plugin/releases/download/6.4-rc/sonar-ruby-plugin-1.0.1.jar 
RUN wget -qP /opt/sonarqube/extensions/plugins https://github.com/hersonalfaro/ruby-sonar-plugin/releases/download/6.4-rc/sonar-ruby-plugin.jar 
