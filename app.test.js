const test = require('node:test');
const assert = require('node:assert');
const { saudar } = require('./app');

test('deve retornar a saudação correta', () => {
  assert.strictEqual(saudar('PETI'), 'Olá, PETI!');
});