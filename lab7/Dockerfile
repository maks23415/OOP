# Use Tomcat with Java 17
FROM tomcat:9.0-jre17

# Set maintainer
LABEL maintainer="your-email@example.com"
LABEL version="1.0"
LABEL description="Lab5 Manual Application with Basic Auth"

# Install PostgreSQL client for health checks
RUN apt-get update && apt-get install -y \
    postgresql-client \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Create application directories
RUN mkdir -p /usr/local/tomcat/logs /app/logs

# Copy WAR file to Tomcat
COPY target/lab5-manual.war /usr/local/tomcat/webapps/ROOT.war

# Copy configuration files
COPY docker/wait-for-db.sh /usr/local/bin/wait-for-db.sh
COPY docker/healthcheck.sh /usr/local/bin/healthcheck.sh

# Make scripts executable
RUN chmod +x /usr/local/bin/wait-for-db.sh /usr/local/bin/healthcheck.sh

# Create a non-root user for security
RUN groupadd -r tomcat && useradd -r -g tomcat tomcat
RUN chown -R tomcat:tomcat /usr/local/tomcat
USER tomcat

# Expose Tomcat port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD /usr/local/bin/healthcheck.sh

# Start Tomcat
CMD ["catalina.sh", "run"]