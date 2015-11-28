<html>
<head>
    <title>${TITLE}</title>
    <style>
        body {
            margin: auto;
            width: 100vw;
            height: 100vh;
            display: flex;
            flex-flow: row wrap;
            align-content: flex-start;
            background-color: #252729;
        }

        #header {
            color: white;
            text-align: center;
            padding: 5px;
            width: 100vw;
        }

        #login {
            position: relative;
            margin: 0 auto;
            padding: 20px 20px 20px;
            width: 310px;
            background: white;
            border-radius: 3px;
        }

    </style>
</head>
<body>
<div id="header">Welcome to the story reader web app</div>
<div id="login"><h1>Login to Web App</h1>
    <form method="post" action="LoginHandler">
        <p><input type="text" name="user" value="" placeholder="Username" required></p>
        <p><input type="password" name="password" value="" placeholder="Password" required></p>
        <p><input type="text" name="email" value="" placeholder="Email" required></p>
        <button type="submit">Submit</button>
    </form>
</div>
</body>
</html>