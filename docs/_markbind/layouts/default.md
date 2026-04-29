{% include "_markbind/layouts/header.md" %}

<div id="flex-body">
  <nav id="site-nav" class="fixed-header-padding">
    <div class="nav-component slim-scroll">
      <site-nav>

- [Introduction]({{ baseUrl }}/index.html)
- [Getting Started]({{ baseUrl }}/getting-started.html)
- Contributing :expanded:
  - [Guidelines]({{ baseUrl }}/contributing/guidelines.html)
  - [Development Guide]({{ baseUrl }}/contributing/development-guide.html)
  - [Development Workflow]({{ baseUrl }}/contributing/development-workflow.html)
- Design
  - [Architecture]({{ baseUrl }}/design/architecture.html)
  - [API Design]({{ baseUrl }}/design/api-design.html)
- Best Practices
  - [Accessibility]({{ baseUrl }}/best-practices/accessibility.html)
  - [Coding]({{ baseUrl }}/best-practices/coding.html)
  - [Mobile-Friendliness]({{ baseUrl }}/best-practices/mobile-friendliness.html)
  - [UI Design]({{ baseUrl }}/best-practices/ui-design.html)
- How-to
  - [CAPTCHA]({{ baseUrl }}/how-to/captcha.html)
  - [Documentation]({{ baseUrl }}/how-to/documentation.html)
  - [Emails]({{ baseUrl }}/how-to/emails.html)
  - [Schema Migration]({{ baseUrl }}/how-to/schema-migration.html)
  - [Static Analysis]({{ baseUrl }}/how-to/static-analysis.html)
  - [Testing]({{ baseUrl }}/how-to/testing.html)
- [Troubleshooting Guide]({{ baseUrl }}/troubleshooting-guide.html)
- [Vision & Principles]({{ baseUrl }}/vision-principles.html)
- [Glossary]({{ baseUrl }}/glossary.html)
</site-nav>
</div>
  </nav>
  <div id="content-wrapper" class="fixed-header-padding">
    <breadcrumb/>
    {{ content }}
  </div>
  <nav id="page-nav" class="fixed-header-padding">
    <div class="nav-component slim-scroll">
      <page-nav />
    </div>
  </nav>
</div>

{% include "_markbind/layouts/footer.md" %}
