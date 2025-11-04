//////////////////////////////////////////////////////
//// @File ui/LauncherWindow.java
//// @Author 0xcds4r
//// @Date 04 Nov. 2025
//////////////////////////////////////////////////////

package org.vxlauncher.ui;

import org.vxlauncher.model.OSType;
import org.vxlauncher.model.ReleaseInfo;
import org.vxlauncher.service.*;
import org.vxlauncher.AppInfo;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LauncherWindow extends JFrame
{
    private static final Color BG_PRIMARY = new Color(18, 18, 18);
    private static final Color BG_SECONDARY = new Color(28, 28, 30);
    private static final Color BG_TERTIARY = new Color(38, 38, 40);
    private static final Color ACCENT_BLUE = new Color(10, 132, 255);
    private static final Color ACCENT_GREEN = new Color(48, 209, 88);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(174, 174, 178);
    private static final Color BORDER_COLOR = new Color(48, 48, 50);

    private JComboBox<String> versionComboBox;
    private JButton installBtn, launchBtn;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel downloadSpeedLabel;
    private final JTextArea logArea = new JTextArea(10, 50);
    private JCheckBox autoLaunchCheck;
    private JPanel versionInfoPanel;
    private JLabel versionSizeLabel;

    private final OSType os = OSType.detectCurrent();
    private Map<String, ReleaseInfo> releases = new HashMap<>();

    private final FileService fileSrv = new FileService();
    private final ReleaseService relSrv;
    private final InstallationService instSrv;
    private final LaunchService launchSrv;

    private long downloadStartTime;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public LauncherWindow()
    {
        log("Инициализация лаунчера...", LogLevel.INFO);

        relSrv = new ReleaseService(os);
        DownloadService dlSrv = new DownloadService();
        instSrv = new InstallationService(fileSrv, dlSrv, os, AppInfo.getVersionsDir());
        launchSrv = new LaunchService(fileSrv, os, AppInfo.getVersionsDir());

        initDirs();
        applyDarkTheme();
        buildUI();
        loadReleases();
    }

    private void applyDarkTheme()
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            UIManager.put("Panel.background", BG_PRIMARY);
            UIManager.put("OptionPane.background", BG_SECONDARY);
            UIManager.put("ComboBox.background", BG_TERTIARY);
            UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
            UIManager.put("ComboBox.selectionBackground", ACCENT_BLUE);
            UIManager.put("ComboBox.selectionForeground", TEXT_PRIMARY);

        } catch (Exception e) {
            log("Не удалось применить системную тему", LogLevel.WARNING);
        }
    }

    private void initDirs()
    {
        log("Инициализация рабочих директорий...", LogLevel.INFO);

        try {
            fileSrv.createDirectories(AppInfo.getAppDir());
            fileSrv.createDirectories(AppInfo.getVersionsDir());
            log("Директории готовы", LogLevel.INFO);
        } catch (IOException e) {
            log("Ошибка создания директорий: " + e.getMessage(), LogLevel.ERROR);
            throw new RuntimeException(e);
        }
    }

    private void buildUI()
    {
        setTitle(AppInfo.getWindowTitle());
        setSize(AppInfo.getWidth() + 100, AppInfo.getHeight() + 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_PRIMARY);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(BG_PRIMARY);

        main.add(createHeaderPanel(), BorderLayout.NORTH);
        main.add(createMainContentPanel(), BorderLayout.CENTER);
        main.add(createLogPanel(), BorderLayout.SOUTH);

        add(main);
        setVisible(true);
    }

    private JPanel createHeaderPanel()
    {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_PRIMARY);
        header.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel title = new JLabel(AppInfo.getAppName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(AppInfo.getAbout());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(8, 0, 0, 0));

        header.add(title);
        header.add(subtitle);

        return header;
    }

    private JPanel createMainContentPanel()
    {
        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(BG_PRIMARY);
        content.setBorder(new EmptyBorder(20, 20, 10, 20));

        content.add(createVersionPanel(), BorderLayout.NORTH);
        content.add(createActionPanel(), BorderLayout.CENTER);
        content.add(createProgressPanel(), BorderLayout.SOUTH);

        return content;
    }

    private JPanel createVersionPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel label = new JLabel("Выберите версию:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);

        versionComboBox = new JComboBox<>();
        versionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        versionComboBox.setFocusable(false);
        versionComboBox.setBackground(BG_TERTIARY);
        versionComboBox.setForeground(TEXT_PRIMARY);
        versionComboBox.addItem("Загрузка версий..");
        versionComboBox.setPreferredSize(new Dimension(0, 38));
        versionComboBox.addActionListener(e -> {
            updateBtns();
            updateVersionInfo();
        });

        versionInfoPanel = new JPanel();
        versionInfoPanel.setLayout(new BoxLayout(versionInfoPanel, BoxLayout.Y_AXIS));
        versionInfoPanel.setBackground(BG_SECONDARY);
        versionInfoPanel.setVisible(false);

        versionSizeLabel = new JLabel();
        versionSizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionSizeLabel.setForeground(TEXT_SECONDARY);
        versionInfoPanel.add(versionSizeLabel);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(BG_SECONDARY);
        top.add(label, BorderLayout.NORTH);
        top.add(versionComboBox, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(versionInfoPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PRIMARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        installBtn = createStyledButton("📥 Установить", ACCENT_BLUE, false);
        installBtn.addActionListener(e -> install());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 1.0;
        panel.add(installBtn, gbc);

        launchBtn = createStyledButton("▶ Запустить", ACCENT_GREEN, false);
        launchBtn.addActionListener(e -> launch());
        gbc.gridx = 1;
        panel.add(launchBtn, gbc);

        JPanel quickAccessPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        quickAccessPanel.setBackground(BG_SECONDARY);
        quickAccessPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel quickLabel = new JLabel("Папки:");
        quickLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        quickLabel.setForeground(TEXT_PRIMARY);
        quickAccessPanel.add(quickLabel);

        quickAccessPanel.add(createIconButton("F", "Папка лаунчера",
                () -> openDir(AppInfo.getAppDir())));
        quickAccessPanel.add(createIconButton("C", "Контент-паки",
                () -> openDir(AppInfo.getContentsDir())));
        quickAccessPanel.add(createIconButton("🌍", "Миры",
                () -> openDir(AppInfo.getWorldsDir())));
        /*quickAccessPanel.add(createIconButton("R", "Очистить кэш",
                this::clearCache));
        quickAccessPanel.add(createIconButton("S", "Настройки",
                this::openSettings));*/

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(quickAccessPanel, gbc);

        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        optionsPanel.setBackground(BG_PRIMARY);

        autoLaunchCheck = new JCheckBox("Запускать сразу после установки");
        autoLaunchCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        autoLaunchCheck.setBackground(BG_PRIMARY);
        autoLaunchCheck.setForeground(TEXT_PRIMARY);
        autoLaunchCheck.setFocusPainted(false);
        optionsPanel.add(autoLaunchCheck);

        gbc.gridy = 2;
        panel.add(optionsPanel, gbc);

        return panel;
    }

    private JPanel createProgressPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setVisible(false);

        statusLabel = new JLabel("Готов к работе");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(TEXT_PRIMARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        downloadSpeedLabel = new JLabel(" ");
        downloadSpeedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        downloadSpeedLabel.setForeground(TEXT_SECONDARY);
        downloadSpeedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        downloadSpeedLabel.setBorder(new EmptyBorder(3, 0, 8, 0));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 28));
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressBar.setForeground(ACCENT_BLUE);
        progressBar.setBackground(BG_TERTIARY);
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(statusLabel);
        panel.add(downloadSpeedLabel);
        panel.add(progressBar);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_PRIMARY);
        wrapper.add(panel, BorderLayout.CENTER);

        progressBar.addChangeListener(e -> {
            if (progressBar.getValue() == 0) {
                wrapper.setVisible(false);
            } else {
                wrapper.setVisible(true);
            }
        });

        return wrapper;
    }

    private JPanel createLogPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(new EmptyBorder(5, 20, 15, 20));

        JPanel logContainer = new JPanel(new BorderLayout());
        logContainer.setBackground(BG_SECONDARY);
        logContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel logTitle = new JLabel("📋 Журнал событий");
        logTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logTitle.setForeground(TEXT_PRIMARY);
        logTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        logArea.setEditable(false);
        logArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 11));
        logArea.setBackground(new Color(25, 25, 27));
        logArea.setForeground(TEXT_SECONDARY);
        logArea.setCaretColor(TEXT_PRIMARY);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.setPreferredSize(new Dimension(0, 120));
        scroll.getViewport().setBackground(new Color(25, 25, 27));

        logContainer.add(logTitle, BorderLayout.NORTH);
        logContainer.add(scroll, BorderLayout.CENTER);

        JButton clearLogBtn = new JButton("Очистить");
        clearLogBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearLogBtn.setFocusPainted(false);
        clearLogBtn.setBackground(BG_TERTIARY);
        clearLogBtn.setForeground(TEXT_PRIMARY);
        clearLogBtn.setBorderPainted(false);
        clearLogBtn.setPreferredSize(new Dimension(85, 28));
        clearLogBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearLogBtn.addActionListener(e -> logArea.setText(""));

        clearLogBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                clearLogBtn.setBackground(new Color(48, 48, 52));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                clearLogBtn.setBackground(BG_TERTIARY);
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        btnPanel.setBackground(BG_SECONDARY);
        btnPanel.add(clearLogBtn);
        logContainer.add(btnPanel, BorderLayout.SOUTH);

        panel.add(logContainer);
        return panel;
    }

    private JButton createStyledButton(String text, Color color, boolean enabled)
    {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 48));
        btn.setEnabled(enabled);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent e)
            {
                if (btn.isEnabled()) {
                    btn.setBackground(color.brighter());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private JButton createIconButton(String icon, String tooltip, Runnable action)
    {
        JButton btn;

        if(icon.equals("F") || icon.equals("C")) {
            btn = new JButton();
        } else {
            btn = new JButton(icon);
        }

        btn.setToolTipText(tooltip);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(46, 42));
        btn.setBackground(BG_TERTIARY);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBorderPainted(false);

        if(icon.equals("F"))
            btn.setIcon(UIHelper.folderIcon());
        else if(icon.equals("C"))
            btn.setIcon(UIHelper.packIcon());

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(48, 48, 52));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(BG_TERTIARY);
            }
        });

        return btn;
    }

    private void openDir(String path)
    {
        File dir = new File(path);

        if (!dir.exists())
        {
            try {
                fileSrv.createDirectories(path);
                log("Создана папка: " + path, LogLevel.SUCCESS);
            } catch (IOException e) {
                log("Ошибка создания папки: " + e.getMessage(), LogLevel.ERROR);
                throw new RuntimeException(e);
            }
        }

        try
        {
            if (os == OSType.WINDOWS)
                Runtime.getRuntime().exec("explorer.exe \"" + dir.getAbsolutePath() + "\"");
            else if (os == OSType.MACOS)
                Runtime.getRuntime().exec(new String[]{"open", dir.getAbsolutePath()});
            else
                Runtime.getRuntime().exec(new String[]{"xdg-open", dir.getAbsolutePath()});

            log("Открыта папка: " + dir.getName(), LogLevel.INFO);
        } catch (Exception ex)
        {
            log("Не удалось открыть папку: " + ex.getMessage(), LogLevel.ERROR);
            showError("Не удалось открыть папку", dir.getAbsolutePath());
        }
    }

    private void clearCache()
    {
        int result = JOptionPane.showConfirmDialog(this,
                "Очистить временные файлы и кэш?\nЭто не удалит установленные версии.",
                "Очистка кэша",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            log("Очистка кэша...", LogLevel.INFO);
            // todo
            log("Кэш очищен", LogLevel.SUCCESS);
        }
    }

    private void openSettings()
    {
        log("Открытие настроек...", LogLevel.INFO);
        JOptionPane.showMessageDialog(this,
                "Окно настроек в разработке",
                "Настройки",
                JOptionPane.INFORMATION_MESSAGE);
        // todo
    }

    private void updateVersionInfo()
    {
        String v = (String) versionComboBox.getSelectedItem();
        if (v == null || !releases.containsKey(v)) {
            versionInfoPanel.setVisible(false);
            return;
        }

        ReleaseInfo info = releases.get(v);
        versionSizeLabel.setText("📦 Размер: ~" + formatSize(info.getSize()));
        versionInfoPanel.setVisible(true);
    }

    private String formatSize(long bytes)
    {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private void loadReleases()
    {
        CompletableFuture.runAsync(() ->
        {
            try
            {
                log("Поиск релизов " + AppInfo.getGameName() + "...", LogLevel.INFO);

                releases = relSrv.loadReleasesFromGitHub();
                SwingUtilities.invokeLater(() ->
                {
                    versionComboBox.removeAllItems();
                    releases.keySet().forEach(versionComboBox::addItem);

                    log("Найдено версий: " + releases.size(), LogLevel.INFO);

                    updateBtns();
                });
            }
            catch (Exception e)
            {
                log("Ошибка загрузки релизов: " + e.getMessage(), LogLevel.ERROR);
            }
        });
    }

    private void install()
    {
        String v = (String) versionComboBox.getSelectedItem();
        if (v == null || !releases.containsKey(v)) return;

        progressBar.getParent().setVisible(true);
        progressBar.setValue(0);
        installBtn.setEnabled(false);
        launchBtn.setEnabled(false);
        downloadStartTime = System.currentTimeMillis();

        SwingUtilities.invokeLater(() ->
                statusLabel.setText("📥 Установка " + v + "...")
        );

        CompletableFuture.runAsync(() ->
        {
            try
            {
                log("📥 Начало установки версии " + v, LogLevel.INFO);

                instSrv.install(releases.get(v),
                        msg -> log(msg, LogLevel.INFO),
                        prog -> {
                            SwingUtilities.invokeLater(() -> {
                                progressBar.setValue(prog);
                                updateDownloadSpeed(prog);
                            });
                        });

                SwingUtilities.invokeLater(() ->
                {
                    progressBar.setValue(100);
                    statusLabel.setText("✓ Установка завершена!");
                    downloadSpeedLabel.setText("Готово!");

                    Timer timer = new Timer(2000, e -> {
                        progressBar.getParent().setVisible(false);
                        progressBar.setValue(0);
                    });
                    timer.setRepeats(false);
                    timer.start();

                    updateBtns();
                    log("Версия " + v + " успешно установлена!", LogLevel.SUCCESS);

                    if (autoLaunchCheck.isSelected()) {
                        Timer launchTimer = new Timer(500, e -> launch());
                        launchTimer.setRepeats(false);
                        launchTimer.start();
                    }
                });
            } catch (Exception e)
            {
                log("Ошибка установки: " + e.getMessage(), LogLevel.ERROR);

                SwingUtilities.invokeLater(() ->
                {
                    progressBar.getParent().setVisible(false);
                    statusLabel.setText("Ошибка установки");
                    installBtn.setEnabled(true);
                    showError("Ошибка установки", e.getMessage());
                });
            }
        });
    }

    private void updateDownloadSpeed(int progress)
    {
        if (progress == 0) return;

        long elapsedTime = (System.currentTimeMillis() - downloadStartTime) / 1000;
        if (elapsedTime > 0) {
            downloadSpeedLabel.setText("Выполняется загрузка..");
        }
    }

    private void launch()
    {
        String v = (String) versionComboBox.getSelectedItem();
        if (v == null) return;

        CompletableFuture.runAsync(() -> {
            try {
                launchSrv.launch(v, msg -> log(msg, LogLevel.INFO));
            }
            catch (Exception e) {
                log("Ошибка запуска: " + e.getMessage(), LogLevel.ERROR);
                showError("Ошибка запуска", e.getMessage());
            }
        });
    }

    private void updateBtns()
    {
        String v = (String) versionComboBox.getSelectedItem();

        if (v == null || v.startsWith("Загрузка версий.."))
        {
            installBtn.setEnabled(false);
            launchBtn.setEnabled(false);
            return;
        }

        boolean installed = instSrv.isVersionInstalled(v);
        installBtn.setEnabled(!installed);
        launchBtn.setEnabled(installed);

        if (installed) {
            installBtn.setText("✓ Установлено");
        } else {
            installBtn.setText("📥 Установить");
        }
    }

    private void showError(String title, String message)
    {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        });
    }

    private void log(String message, LogLevel level)
    {
        SwingUtilities.invokeLater(() -> {
            String icon = switch(level) {
                case INFO -> "";
                case SUCCESS -> " ✓";
                case WARNING -> " !";
                case ERROR -> " ✗";
            };

            String timestamp = timeFormat.format(new Date());
            logArea.append(String.format("[%s]%s %s\n", timestamp, icon, message));
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private enum LogLevel {
        INFO, SUCCESS, WARNING, ERROR
    }
}