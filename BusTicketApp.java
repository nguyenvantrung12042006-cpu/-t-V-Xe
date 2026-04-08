import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BusTicketApp extends JFrame {
    private JPanel ticketContainer;
    private JTextField txtFrom, txtTo;
    private List<Trip> allTrips = new ArrayList<>();

    public BusTicketApp() {
        setTitle("Hệ Thống Đặt Vé Xe - VEXERE QUE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. HEADER & ADMIN LOGIN ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel lblTitle = new JLabel(" 🚍 VEXERE QUE", SwingConstants.LEFT);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setBorder(new EmptyBorder(0, 20, 0, 0));

        JButton btnAdmin = new JButton("Đăng nhập Admin");
        btnAdmin.setBackground(new Color(192, 57, 43));
        btnAdmin.setForeground(Color.WHITE);
        btnAdmin.addActionListener(e -> openLoginDialog());

        JPanel authPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        authPanel.setOpaque(false);
        authPanel.add(btnAdmin);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(authPanel, BorderLayout.EAST);

        // --- 2. SEARCH BAR ---
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        txtFrom = new JTextField(12);
        txtTo = new JTextField(12);
        JButton btnSearch = new JButton("TÌM CHUYẾN");
        btnSearch.setBackground(new Color(230, 126, 34));
        btnSearch.setForeground(Color.WHITE);

        searchBar.add(new JLabel("Điểm đi:"));
        searchBar.add(txtFrom);
        searchBar.add(new JLabel("Điểm đến:"));
        searchBar.add(txtTo);
        searchBar.add(btnSearch);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(searchBar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // --- 3. TICKET LIST CONTAINER ---
        ticketContainer = new JPanel();
        ticketContainer.setLayout(new BoxLayout(ticketContainer, BoxLayout.Y_AXIS));
        ticketContainer.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(ticketContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // --- 4. INITIALIZE DATA ---
        initData();
        renderTickets(allTrips); // Hiện tất cả lúc khởi động

        // Sự kiện tìm kiếm
        btnSearch.addActionListener(e -> performSearch());
    }

    private void initData() {
        allTrips.add(new Trip("Hải Vân Express", "08:00", "Hà Nội", "Sapa", "250000", "Limousine"));
        allTrips.add(new Trip("Thành Bưởi", "10:30", "Sài Gòn", "Đà Lạt", "270000", "Giường nằm"));
        allTrips.add(new Trip("Phương Trang", "14:00", "Sài Gòn", "Cần Thơ", "190000", "Sleeper Bus"));
        allTrips.add(new Trip("Hoàng Long", "19:00", "Hà Nội", "Hải Phòng", "150000", "Ghế ngồi"));
    }

    private void performSearch() {
        String from = txtFrom.getText().toLowerCase().trim();
        String to = txtTo.getText().toLowerCase().trim();
        List<Trip> filtered = allTrips.stream()
                .filter(t -> t.from.toLowerCase().contains(from) && t.to.toLowerCase().contains(to))
                .collect(Collectors.toList());
        renderTickets(filtered);
    }

    private void renderTickets(List<Trip> trips) {
        ticketContainer.removeAll();
        for (Trip t : trips) {
            ticketContainer.add(createCard(t));
            ticketContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        ticketContainer.revalidate();
        ticketContainer.repaint();
    }

    private JPanel createCard(Trip t) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setMaximumSize(new Dimension(950, 130));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        // Thông tin nhà xe
        JLabel lblName = new JLabel("<html><b>" + t.name + "</b><br><font color='#3498db'>" + t.type + "</font></html>");
        lblName.setBorder(new EmptyBorder(0, 20, 0, 0));
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Lộ trình
        JLabel lblRoute = new JLabel("<html><center>🕒 " + t.time + "<br>" + t.from + " ➔ " + t.to + "</center></html>", SwingConstants.CENTER);
        lblRoute.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Giá & Nút chọn chỗ
        JPanel right = new JPanel(new GridLayout(2, 1, 0, 5));
        right.setOpaque(false);
        JLabel lblPrice = new JLabel(String.format("%,dđ", Integer.parseInt(t.price)), SwingConstants.RIGHT);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPrice.setForeground(new Color(211, 84, 0));

        JButton btnSelect = new JButton("CHỌN CHỖ");
        btnSelect.setBackground(new Color(39, 174, 96));
        btnSelect.setForeground(Color.WHITE);
        btnSelect.setFocusPainted(false);

        // QUAN TRỌNG: Gắn lại sự kiện chọn chỗ ở đây
        btnSelect.addActionListener(e -> openSeatMap(t));

        right.add(lblPrice);
        right.add(btnSelect);

        card.add(lblName, BorderLayout.WEST);
        card.add(lblRoute, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private void openSeatMap(Trip trip) {
        JDialog dialog = new JDialog(this, "Chọn ghế: " + trip.name, true);
        dialog.setSize(400, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel grid = new JPanel(new GridLayout(10, 4, 10, 10));
        grid.setBorder(new EmptyBorder(20, 30, 20, 30));
        grid.setBackground(Color.WHITE);

        List<String> selectedSeats = new ArrayList<>();
        JLabel lblStatus = new JLabel("Đã chọn: 0 ghế | Tổng: 0đ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 15));

        for (int i = 1; i <= 40; i++) {
            String seatId = "G" + i;
            JButton btnSeat = new JButton(seatId);
            btnSeat.setBackground(Color.WHITE);
            btnSeat.addActionListener(e -> {
                if (btnSeat.getBackground() == Color.WHITE) {
                    btnSeat.setBackground(Color.YELLOW);
                    selectedSeats.add(seatId);
                } else {
                    btnSeat.setBackground(Color.WHITE);
                    selectedSeats.remove(seatId);
                }
                long total = (long) selectedSeats.size() * Integer.parseInt(trip.price);
                lblStatus.setText("Đã chọn: " + selectedSeats.size() + " ghế | Tổng: " + String.format("%,dđ", total));
            });
            grid.add(btnSeat);
        }

        JButton btnConfirm = new JButton("XÁC NHẬN ĐẶT VÉ");
        btnConfirm.setBackground(new Color(44, 62, 80));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setPreferredSize(new Dimension(0, 50));
        btnConfirm.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn ít nhất 1 ghế!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Đặt vé thành công nhà xe " + trip.name + "\nGhế: " + selectedSeats);
                dialog.dispose();
            }
        });

        dialog.add(new JScrollPane(grid), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblStatus, BorderLayout.NORTH);
        bottom.add(btnConfirm, BorderLayout.SOUTH);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openLoginDialog() {
        JDialog loginDialog = new JDialog(this, "Admin Login", true);
        loginDialog.setSize(300, 200);
        loginDialog.setLayout(new GridLayout(3, 1, 10, 10));
        loginDialog.setLocationRelativeTo(this);

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        loginDialog.add(new JLabel("  User:", SwingConstants.LEFT));
        loginDialog.add(user);
        loginDialog.add(new JLabel("  Pass:", SwingConstants.LEFT));
        loginDialog.add(pass);

        JButton login = new JButton("ĐĂNG NHẬP");
        login.addActionListener(e -> {
            if (user.getText().equals("admin") && new String(pass.getPassword()).equals("123")) {
                JOptionPane.showMessageDialog(loginDialog, "Chào mừng Admin!");
                loginDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Sai thông tin!");
            }
        });
        loginDialog.add(login);
        loginDialog.setVisible(true);
    }

    class Trip {
        String name, time, from, to, price, type;
        Trip(String n, String t, String f, String to, String p, String ty) {
            this.name = n; this.time = t; this.from = f; this.to = to; this.price = p; this.type = ty;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BusTicketApp().setVisible(true));
    }
}