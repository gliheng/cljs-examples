echo "Initial build..."
./node_modules/.bin/node-sass resources/styles/style.scss  --output resources/public/css/
echo "Watching style changes..."
./node_modules/.bin/node-sass resources/styles/style.scss  --output resources/public/css/ --watch
