import path from 'path';
import ForkTsCheckerWebpackPlugin from 'fork-ts-checker-webpack-plugin';
import ESLintPlugin from 'eslint-webpack-plugin';
import { WebpackManifestPlugin } from 'webpack-manifest-plugin';
import MiniCssExtractPlugin from 'mini-css-extract-plugin';
import { Configuration, EnvironmentPlugin } from 'webpack';
import { Configuration as DevServer } from 'webpack-dev-server';
import { env } from './env';

const OUTPUT_PATH = path.join(__dirname, 'src/main/resources/static/bundles');

const buildConfig = (
  webpackEnv: Record<string, boolean>
): Configuration & { devServer: DevServer } => {
  const isProd = webpackEnv.production;
  const mode: Configuration['mode'] = isProd ? 'production' : 'development';
  const { WS_URL, HOST } = env[mode];

  const plugins = [
    new EnvironmentPlugin({ WS_URL }),
    new ForkTsCheckerWebpackPlugin(),
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
  ].filter((plugin): plugin is Exclude<typeof plugin, false> => Boolean(plugin));

  return {
    mode,
    entry: {
      common: {
        import: './src/frontend/pages/common.ts'
      },
      // Split the main vendors into its own chunk and make sure the other chunks
      // are set to depend on them.
      vendor: ['react', 'react-dom'],
      home: {
        import: './src/frontend/pages/home/index.ts',
        dependOn: 'vendor'
      },
      singlePlayer: {
        import: './src/frontend/pages/single-player/index.tsx',
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
          test: /\.[jt]sx?$/i,
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
        '**': `http://${HOST}`
      },
      port: 4000,
      open: true,
      hot: true
    }
  };
};

export default buildConfig;
