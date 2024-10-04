module.exports = {
  preset: 'jest-preset-angular',
  globalSetup: 'jest-preset-angular/global-setup',  // Można usunąć tę linię, aby uniknąć ostrzeżenia
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  transform: {
    '^.+\\.(ts|js|html)$': ['ts-jest', {
      tsconfig: './tsconfig.spec.json',
      stringifyContentPathRegex: '\\.html$',
      astTransformers: {
        before: ['jest-preset-angular/build/InlineHtmlStripStylesTransformer'],
      },
      isolatedModules: true,
      preserveSymlinks: true,
    }],
  },
  transformIgnorePatterns: ['node_modules/(?!.*.mjs$|@datorama/akita)'],
  moduleFileExtensions: ['ts', 'html', 'js', 'json'],
  moduleDirectories: ['node_modules', 'src'],
  modulePaths: ['<rootDir>'],
  moduleNameMapper: {
    '^@app/(.*)$': ['<rootDir>/src/app/$1'],
  },
};
