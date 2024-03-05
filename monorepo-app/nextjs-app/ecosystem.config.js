module.exports = {
  apps: [
    {
      name: 'ne',
      script: 'npm',
      args: 'run start',
      instances: 'max',
      autorestart: true,
      watch: false,
      max_memory_restart: '1G',
      env: {
        NODE_ENV: 'production',
        PORT: 3002,
      },
    },
  ],
};
