interface EnvVar {
  HOST: string;
  WS_URL: string;
}

const DEV_HOST = 'localhost:8080';
const PROD_HOST = 'team-sweeper.cphillipsdorsett.com';
const WS_PATH = 'game/publish';

/**
 * Non-sensitive FE environment variables.
 *
 * These should ONLY be imported into the webpack config file. Any env variables
 * that we want to expose to the application need to be declared with the
 * `DefinePlugin`.
 */
export const env: Record<'development' | 'production', EnvVar> = {
  development: {
    HOST: DEV_HOST,
    WS_URL: `ws://${DEV_HOST}/${WS_PATH}`
  },
  production: {
    HOST: PROD_HOST,
    WS_URL: `wss://${PROD_HOST}/${WS_PATH}`
  }
};
