module.exports = {
  apps: [
    {
      name: 'monorepo',
      script: 'yarn',
      args: 'start',
      instances: 'max',
      autorestart: true,
      max_memory_restart: '1G',
      cwd: '/home/web-h-010/yc-workspace/Yatharth-Chauhan/yatharth-new-pm2/training/new-monorepo',
      env: {
        NODE_ENV: 'production',
        PORT: 3000
      }
    }
  ]
};
