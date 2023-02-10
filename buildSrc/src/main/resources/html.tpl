<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <base href="/">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>{{title}}</title>
    <style>
        @import 'https://fonts.googleapis.com/css?family=Quicksand:300,400,500';
        * {
            font-size: 1em;
        }
        body {
            font-family: "Quicksand", "Helvetica", "Arial", sans-serif;
            color: #555;
            line-height: 1.5;
            margin: 0;
        }

        header {
            min-height: 50px;
            padding: 0;
            background-color: #404040;
            color: #fff;
        }

        header img {
           max-height: 50px;
           width: auto;
           display: inline-block;
           margin-left: 0;
           margin-right: auto;
           height: 100%;
        }

        a {
            color: #004eff;
        }

        .links {
            display: inline-block;
            margin-left: auto;
            margin-right: 0;
            float: right;
            padding-top: 15px;
        }

        .links a {
            color: #fff;
            padding-left: 2em;
        }

        main, .content {
            max-width: 50em;
            margin: 0 auto;
            padding: 1em;
        }

        .content {

        }
        h1, h1 code {
            font-size: 1.5em;
        }
        h2 {
            margin-top: 1em;
            padding-top: 1em;
            font-size: 1.2em;
        }

        h1, h2, strong, h1 a {
            color: #333;
        }

        code, pre {
            background: #eee;
        }

        code {
            padding: 2px 4px;
            vertical-align: text-bottom;
        }

        pre {
            padding: 1em;
            display: block;
            unicode-bidi: embed;
            font-family: monospace;
            white-space: pre;
        }

        pre, var {
            font-weight: 500;
        }

        kbd {
            background-color: #c0c0c0;
            color: #000000;
            margin-left: 0.5em;
            padding: 5px;
        }

        ul.related {
            padding-left: 0;
            list-style: none;
        }

        ul.related li {
            display: inline-block;
            margin-right: 0.5rem;
        }

        var {
            color: #404040;
            margin-left: 0.5em;
        }

        .siginlinetype {
            color: #a0a0a0;
        }

        table {
            border-collapse: collapse;
            margin: 25px 0;
            font-size: 0.9em;
            font-family: sans-serif;
            min-width: 400px;
            width: 100%;
        }

        table thead tr {
            background-color: #404040;
            color: #ffffff;
            text-align: left;
        }

        table th,
        table td {
            padding: 12px 15px;
        }

        table tbody tr {
            border-bottom: 1px solid #dddddd;
        }

        table tbody tr:nth-of-type(even) {
            background-color: #f3f3f3;
        }

        table tbody tr:last-of-type {
            border-bottom: 2px solid #404040;
        }
        @page {
            size: a4;
            margin: 0;
            @bottom-right {
                 content: counter(page) '/' counter(pages);
            }
        }
        @media print {
            main, .content {
                max-width: 100%;
            }
            * {
                font-size: 12px;
            }
            h1, h1 code {
                font-size: 16px;
                 page-break-before : always;
            }
            h2 {
                font-size: 14px;
                page-break-after : avoid;
            }
        }
    </style>
</head>
<body>
    <header>
        <div class="content">
            <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAANkAAABkCAMAAAA8NGXvAAABGlBMVEUAAAD///////////8ATv/29vb///8ATv////8ATv/////29vb///8ATv8ATv////////8ATv////////////////////8ATv////////////////8ATv////////////////////////////////////////8ATv////8ATv8ATv8ATv////8ATv8ATv////////8ATv////////8ATv8ATv////////8ATv////////8ATv8ATv8ATv8ATv8ATv8ATv8ATv/////////AwMAATv////////////////////8ATv/AwMAATv/AwMD///8ATv/AwMAATv/AwMDAwMDAwMAATv/AwMD////AwMDAwMDAwMAATv////+DKpFVAAAAXHRSTlMA8APt+wfn9yoHQw4s59gWUxvZkGZ8SQ6cMc+jooch4bQm9XNaTh3NyK2ASzsrIhm8tq5tZjgS1Jk+OTAW75SKc0Hj3NrGw7+ploJrIrylX18y4E7kiFZmNXlg91P5I5oAAAiESURBVHja3NjpUhpBFAXg0w0DwiiIiCCLoBI1GkXFDbeoUXHHLTGa7vd/jShCHKZvz1KVAsrvH1UDxZnuvvfOoG9FYicRfEqXQmzhU4oJIW7xCS2IV3tL8O7u5xvz4Q6vngz0q2vxZjYCz66enh+fXoybYwN4+HOFPrUTFE3f4cPdMYCb458wj/s3WV68Cy74TvZyfHfz8tivyRJh0TK06zfZw8PjDfo22bz4Z8V3MjxdOSXjk8OjM+iRyJD4cOA7GaBLZtQu6kxKWUePrAmL8A48Mp8BPDQzPRtQ8LFcQLYMoDdiwioWgat0Gi7iqSP5IYmeWBCdtty/sXfvkqswJa0CJnrhWtgswNlBUATT0OPVgLSpoguILm3hXvojW+LVDrQm56TiB0f35YViBXrpFZd1LU9JwjC6QOnSqkv95bPOV/CCJM2h6+YFIbwI2uK9cJww+bnUiKILlC6t0E39h3ui5StIBamzjC5LhwVpHoRf4Y/ooJSl3ja6bE2QgutQXAYt9ROEGSb1Sui2fUGKRZxOJN3QzqSTcXRZZEWQ9tFpS3RYhGJcOlpFt6Vj9H487Mifdx9UMlIjtJzL1YsldN3SnqDcp6mF1bc8g0kKKwxw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" alt="Senx">
            <div class="links">
                <a href="https://warp10.io/" target="_blank">Warp 10</a>
                <a href="https://senx.io/" target="_blank">SenX</a>
            </div>
        </div>
    </header>
    <main>
    {{content}}
    </main>
</body>
</html>
