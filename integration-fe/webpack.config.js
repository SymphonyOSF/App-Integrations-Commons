var path = require("path"),
  CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  entry: {
    controller: path.resolve(__dirname, "./src/js/controller.js"),
    app: path.resolve(__dirname, "./src/js/app.jsx")
  },
  output: {
    path: path.resolve(__dirname, "dist"),
    filename: "[name].bundle.js"
  },
  evtool: 'source-map',
  module: {
    loaders: [
      { test: /\.css$/, loader: "style!css" },
      { test: /\.less$/, loader: "style!css!less" },
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        loader: 'babel', // loaders: ['babel?presets[]=es2015,presets[]=react', 'eslint-loader']
        query: {
          presets: ['react', 'es2015'],
          plugins: ['transform-object-rest-spread'],
        }
      },
      { test: /\.(jpe?g|png|gif|svg)$/i, loader: 'url?limit=10000!img?progressive=true' },
      { test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/font-woff' },
      { test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/octet-stream' },
      { test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: 'file' },
      { test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=image/svg+xml' }
    ]
  },
  resolve: {
    extensions: ['', '.js', '.jsx']
  },
  eslint: {
    configFile: './.eslintrc.js'
  },
  plugins: [
    new CopyWebpackPlugin([{
      from: './src/html/app.html'
    }]),
    new CopyWebpackPlugin([{
      from: './src/html/controller.html'
    }]),
    new CopyWebpackPlugin([{
      from: './src/bundle.json'
    }]),
  ],
  devServer: {
    contentBase: path.resolve(__dirname, 'dist'),
    port: 4000,
    inline: true,
    headers: {
      "Access-Control-Allow-Origin": "*"
    }
  }
};