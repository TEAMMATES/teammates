echo "Installing NodeJS"
apt-get update
curl -sL https://deb.nodesource.com/setup_10.x | bash -
apt-get install -y nodejs

echo "Swapping out 'localhost' in build.gradle for docker container hostname 'tmserver_container' to allow for proper binding"
sed -i -e 's/"localhost"/"teammatescontainer"/' build.gradle

echo "Building application"
npm install

# Convert line endings and set executable flag to ensure Windows compatibility
sed -i 's/\r//g' gradlew
chmod a+x gradlew
./gradlew createConfigs
npm run dev
