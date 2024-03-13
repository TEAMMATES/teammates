<head-bottom>
  <link rel="stylesheet" href="{{ baseUrl }}/stylesheets/main.css">
</head-bottom>

<header sticky>
  <navbar type="inverse">
    <a slot="brand" href="{{ baseUrl }}/index.html" title="Home" class="navbar-brand">
      <pic src="{{ baseUrl }}/images/teammateslogo.png" width="150" alt="Logo" caption=""/>
      <span style="font-style:italic;font-size:small">[dev docs]</span>
    </a>
    <li><a href="{{ baseUrl }}/index.html" class="nav-link">Home</a></li>
    <li><a href="{{ baseUrl }}/contributing-doc.html" class="nav-link">Contributing</a></li>
    <li><a href="https://teammatesv4.appspot.com/" target="_blank" class="nav-link">Product Website <md>:glyphicon-share-alt:</md></a></li>
    <li><a href="https://github.com/TEAMMATES/teammates" target="_blank" class="nav-link"><md>:fab-github:</md></a></li>
  <li slot="right">
    <form class="navbar-form">
      <searchbar :data="searchData" placeholder="Search" :on-hit="searchCallback" menu-align-right></searchbar>
    </form>
  </li>
  </navbar>
</header>
