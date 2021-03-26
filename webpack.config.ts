import path from 'path';
import webpack from 'webpack';
import ForkTsCheckerWebpackPlugin from 'fork-ts-checker-webpack-plugin';
import ESLintPlugin from 'eslint-webpack-plugin';
import { WebpackManifestPlugin } from 'webpack-manifest-plugin';
import MiniCssExtractPlugin from 'mini-css-extract-plugin';

const isProd = process.env.NODE_ENV === 'production';
const OUTPUT_PATH = path.join(__dirname, 'src/main/resources/static/bundles');

const plugins = [
  new ForkTsCheckerWebpackPlugin(),
  new webpack.HotModuleReplacementPlugin(),
  new ESLintPlugin({ extensions: ['js', 'jsx', 'ts', 'tsx'] }),
  new WebpackManifestPlugin({
    // Only return the basename for the keys with the preceding path
    map: (fileDescriptor) => {
      return {
        ...fileDescriptor,
        name: path.basename(fileDescriptor.name || '')
      };
    }
  }),
  isProd &&
    new MiniCssExtractPlugin({
      filename: '[name].[contenthash].css'
    })
].filter(Boolean);

const config = {
  mode: isProd ? 'production' : 'development',
  entry: {
    main: {
      import: './src/frontend/index.tsx'
    },
    // Split the main vendors into its own chunk and make sure the other chunks
    // are set to depend on them.
    vendor: ['react', 'react-dom'],
    home: {
      import: './src/frontend/pages/home/Home.tsx',
      dependOn: 'vendor'
    }
  },
  output: {
    filename: `[name].${isProd ? '[contenthash].' : ''}js`,
    path: OUTPUT_PATH,
    publicPath: '/bundles/',
    clean: true
  },
  module: {
    rules: [
      {
        test: /\.(ts|js)x?$/i,
        exclude: /node_modules/,
        use: { loader: 'ts-loader', options: { transpileOnly: true } }
      },
      {
        test: /\.less$/i,
        use: [isProd ? MiniCssExtractPlugin.loader : 'style-loader', 'css-loader', 'less-loader']
      },
      {
        test: /\.(png|jpe?g|svg|ttf|woff2?)$/i,
        type: 'asset/resource',
        generator: {
          filename: '[base]'
        }
      }
    ]
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js']
  },
  plugins,
  devtool: isProd ? false : 'inline-source-map',
  devServer: {
    proxy: {
      '**': 'http://localhost:8080/'
    },
    port: 4000,
    open: true,
    hot: true
  }
};

export default config;
