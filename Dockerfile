FROM google/cloud-sdk:latest
ADD /. /app/
WORKDIR /app
USER root

# Convert line endings and set executable flag to ensure Windows compatibility
RUN sed -i 's/\r//g' bootstrap.sh
RUN chmod +x bootstrap.sh
RUN ./bootstrap.sh
