module.exports = {
    apps: [
      {
        name: 'vue-app',
        script: 'yarn',
        args: 'run serve',
        cwd: '/home/ubuntu/my-vue-app',
        watch: true,
        ignore_watch: ['node_modules'],
        env: {
          NODE_ENV: 'production'
        }
      }
    ]
  };
  