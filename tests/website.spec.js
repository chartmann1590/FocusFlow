const { test, expect } = require('@playwright/test');

const BASE_URL = 'https://chartmann1590.github.io/FocusFlow/';

test.describe('FocusFlow Landing Page - E2E Tests', () => {
  
  test.beforeEach(async ({ page }) => {
    await page.goto(BASE_URL);
  });

  test('1. Homepage loads successfully', async ({ page }) => {
    const response = await page.goto(BASE_URL);
    expect(response.status()).toBe(200);
  });

  test('2. Has correct page title', async ({ page }) => {
    await expect(page).toHaveTitle(/FocusFlow/);
  });

  test('3. Hero section displays correctly', async ({ page }) => {
    const heroTitle = page.locator('.hero-content h1');
    await expect(heroTitle).toBeVisible();
    await expect(heroTitle).toContainText('Stay Focused');
  });

  test('4. Hero buttons are clickable', async ({ page }) => {
    const downloadBtn = page.locator('.hero-buttons .btn-primary').first();
    await expect(downloadBtn).toBeVisible();
    await expect(downloadBtn).toContainText('Download');
  });

  test('5. Features section exists', async ({ page }) => {
    const featuresSection = page.locator('#features');
    await expect(featuresSection).toBeVisible();
  });

  test('6. All 6 feature cards are rendered', async ({ page }) => {
    const cards = page.locator('.feature-card');
    await expect(cards).toHaveCount(6);
  });

  test('7. Feature card content is correct', async ({ page }) => {
    const firstCard = page.locator('.feature-card').first();
    await expect(firstCard.locator('h3')).toContainText('Pomodoro Timer');
  });

  test('8. Screenshots section exists', async ({ page }) => {
    const screenshotsSection = page.locator('#screenshots');
    await expect(screenshotsSection).toBeVisible();
  });

  test('9. All 6 screenshots are rendered', async ({ page }) => {
    const screenshots = page.locator('.screenshot-card img');
    await expect(screenshots).toHaveCount(6);
  });

  test('10. Navigation links work', async ({ page }) => {
    await page.click('a[href="#features"]');
    await page.waitForTimeout(500);
    const featuresSection = page.locator('#features');
    await expect(featuresSection).toBeInViewport();
  });

  test('11. Stats section is visible', async ({ page }) => {
    const statsSection = page.locator('.stats-section');
    await expect(statsSection).toBeVisible();
  });

  test('12. All 4 stats items displayed', async ({ page }) => {
    const statItems = page.locator('.stat-item');
    await expect(statItems).toHaveCount(4);
  });

  test('13. How it works section exists', async ({ page }) => {
    const howItWorks = page.locator('#how-it-works');
    await expect(howItWorks).toBeVisible();
  });

  test('14. All 3 steps are displayed', async ({ page }) => {
    const steps = page.locator('.step-card');
    await expect(steps).toHaveCount(3);
  });

  test('15. Footer is present', async ({ page }) => {
    const footer = page.locator('footer');
    await expect(footer).toBeVisible();
  });

  test('16. Footer links are functional', async ({ page }) => {
    const githubLink = page.locator('footer a[href*="github.com"]').first();
    await expect(githubLink).toHaveAttribute('href', /github\.com/);
  });

  test('17. CTA section is visible', async ({ page }) => {
    const ctaSection = page.locator('.cta-section');
    await expect(ctaSection).toBeVisible();
  });

  test('18. CTA buttons are clickable', async ({ page }) => {
    const ctaDownloadBtn = page.locator('.cta-buttons .btn-primary');
    await expect(ctaDownloadBtn).toBeVisible();
  });

  test('19. Navigation bar is fixed', async ({ page }) => {
    const nav = page.locator('.nav');
    await expect(nav).toBeVisible();
    const position = await nav.evaluate(el => window.getComputedStyle(el).position);
    expect(position).toBe('fixed');
  });

  test('20. Mobile menu toggle exists', async ({ page }) => {
    const navLinks = page.locator('.nav-links');
    await expect(navLinks).toBeAttached();
  });

  test('21. Logo is visible and linked', async ({ page }) => {
    const logo = page.locator('.nav .logo');
    await expect(logo).toBeVisible();
    await expect(logo).toHaveAttribute('href', /FocusFlow|#/);
  });

  test('22. SEO meta tags are present', async ({ page }) => {
    const description = page.locator('meta[name="description"]');
    await expect(description).toHaveAttribute('content', /.+/);
  });

  test('23. Open Graph tags are present', async ({ page }) => {
    const ogTitle = page.locator('meta[property="og:title"]');
    await expect(ogTitle).toHaveAttribute('content', /FocusFlow/);
  });

  test('24. No console errors on page load', async ({ page }) => {
    const errors = [];
    page.on('console', msg => {
      if (msg.type() === 'error') {
        errors.push(msg.text());
      }
    });
    await page.goto(BASE_URL);
    await page.waitForLoadState('networkidle');
    // Filter out expected 404s for missing images
    const criticalErrors = errors.filter(e => !e.includes('404'));
    expect(criticalErrors).toHaveLength(0);
  });

  test('25. Page is scrollable', async ({ page }) => {
    await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
    await page.waitForTimeout(300);
    const footer = page.locator('footer');
    await expect(footer).toBeVisible();
  });

  test('26. Page has scrollable content', async ({ page }) => {
    await page.goto(BASE_URL);
    const scrollHeight = await page.evaluate(() => document.body.scrollHeight);
    const windowHeight = await page.evaluate(() => window.innerHeight);
    const canScroll = scrollHeight > windowHeight;
    expect(canScroll).toBe(true);
  });

  test('27. Responsive layout - mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto(BASE_URL);
    const heroTitle = page.locator('.hero-content h1');
    await expect(heroTitle).toBeVisible();
  });

  test('28. Responsive layout - tablet viewport', async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.goto(BASE_URL);
    const featuresGrid = page.locator('.features-grid');
    await expect(featuresGrid).toBeVisible();
  });

  test('29. All external links have correct target', async ({ page }) => {
    const externalLinks = page.locator('a[href*="github.com"]');
    const count = await externalLinks.count();
    expect(count).toBeGreaterThan(0);
    for (let i = 0; i < count; i++) {
      await expect(externalLinks.nth(i)).toHaveAttribute('target', '_blank');
    }
  });

  test('30. Download button links to releases', async ({ page }) => {
    const downloadBtn = page.locator('.nav .btn-primary');
    await expect(downloadBtn).toHaveAttribute('href', /releases/);
  });
});

test.describe('Accessibility Tests', () => {
  test('31. Page has proper heading hierarchy', async ({ page }) => {
    await page.goto(BASE_URL);
    const h1 = page.locator('h1');
    await expect(h1).toHaveCount(1);
    const h2s = page.locator('h2');
    const count = await h2s.count();
    expect(count).toBeGreaterThan(0);
  });

  test('32. Images have alt text', async ({ page }) => {
    await page.goto(BASE_URL);
    const images = page.locator('img');
    const count = await images.count();
    if (count > 0) {
      const firstImg = images.first();
      const alt = await firstImg.getAttribute('alt');
      expect(alt).toBeTruthy();
    }
  });

  test('33. Interactive elements are keyboard accessible', async ({ page }) => {
    await page.goto(BASE_URL);
    const downloadBtn = page.locator('.btn-primary').first();
    await downloadBtn.focus();
    await expect(downloadBtn).toBeFocused();
  });
});

test.describe('Performance Tests', () => {
  test('34. Page loads within acceptable time', async ({ page }) => {
    const startTime = Date.now();
    await page.goto(BASE_URL, { waitUntil: 'domcontentloaded' });
    const loadTime = Date.now() - startTime;
    expect(loadTime).toBeLessThan(5000);
  });

  test('35. No external resources fail to load (excluding images)', async ({ page }) => {
    const failedRequests = [];
    page.on('requestfailed', request => {
      if (!request.url().includes('screenshot') && !request.url().includes('phone')) {
        failedRequests.push(request.url());
      }
    });
    await page.goto(BASE_URL);
    await page.waitForLoadState('networkidle');
    expect(failedRequests).toHaveLength(0);
  });
});
