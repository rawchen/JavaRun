https://microsoft.github.io/monaco-editor/index.html

该文件夹可使用monaco-editor离线资源，使用min文件夹即可。

资源文件位置格式：

resources -> static -> monaco-editor(即为原来的min文件夹) -> vs

```js
var myEditor;
// require.config({ paths: { 'vs': '../static/monaco-editor/vs' }});
require.config({ paths: { 'vs': '//cdn.rawchen.com/vs' }});
require(['vs/editor/editor.main'], function() {
    myEditor = monaco.editor.create(document.getElementById('container'), {
        value: [
            'public class Main {\n' +
            '\tpublic static void main(String[] args) {\n' +
            '\t\tSystem.out.println("hello world!");\n' +
            '\t}\n' +
            '}'
        ].join('\n'),
        language: 'java',
        theme: 'vs-dark',
        fontSize: 16,
        scrollBeyondLastLine: false,
    });
});
```

