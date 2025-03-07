{% include "_markbind/layouts/header.md" %}

<div id="flex-body">
  <nav id="site-nav" class="fixed-header-padding">
    <div class="nav-component slim-scroll">
      <site-nav>

* About TEAMMATES :expanded:
  * [Introduction]({{ baseUrl }}/index.html)
  * [Overview]({{ baseUrl }}/overview.html)
* Getting Started :expanded:
  * [Setting Up]({{ baseUrl }}/setting-up.html)
* [Contributing]({{ baseUrl }}/contributing-doc.html)
* Workflow :expanded:
  * [Issues]({{ baseUrl }}/issues.html)
  * [Process]({{ baseUrl }}/process.html)
  * [Development]({{ baseUrl }}/development.html)
* [Design]({{ baseUrl }}/design.html)
* Best Practices :expanded:
  * [Coding]({{ baseUrl }}/best-practices/coding.html)
  * [Testing]({{ baseUrl }}/best-practices/testing.html)
  * [Data Migration]({{ baseUrl }}/best-practices/data-migration.html)
  * [UI Design]({{ baseUrl }}/best-practices/ui-design.html)
  * [Accessibility]({{ baseUrl }}/best-practices/accessibility.html)
  * [Mobile-Friendliness]({{ baseUrl }}/best-practices/mobile-friendliness.html)
* How-to :expanded:
  * [Captcha]({{ baseUrl }}/captcha.html)
  * [Documentation]({{ baseUrl }}/documentation.html)
  * [Emails]({{ baseUrl }}/emails.html)
  * [Unit Testing]({{ baseUrl }}/unit-testing.html)
  * [End-to-End Testing]({{ baseUrl }}/e2e-testing.html)
  * [Snapshot Testing]({{ baseUrl }}/snapshot-testing.html)
  * [Performance Testing]({{ baseUrl }}/performance-testing.html)
  * [Accessibility Testing]({{ baseUrl }}/axe-testing.html)
  * [Search]({{ baseUrl }}/search.html)
  * [Static Analysis]({{ baseUrl }}/static-analysis.html)
* [Troubleshooting Guide]({{ baseUrl }}/troubleshooting-guide.html)
* [Glossary]({{ baseUrl }}/glossary.html)
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
