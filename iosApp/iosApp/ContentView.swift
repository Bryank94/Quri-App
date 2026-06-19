import SwiftUI

struct ContentView: View {
    @AppStorage("quriUserName") private var userName: String = "Bryan"
    @State private var tab: QuriTab = .inicio
    @State private var showProfile = false
    @State private var showAdvice = false
    @State private var funds = QuriFund.demo
    @State private var movements = QuriMovement.demo

    private var summary: QuriSummary {
        QuriSummary(funds: funds, movements: movements)
    }

    var body: some View {
        ZStack {
            QuriBackground().ignoresSafeArea()

            VStack(spacing: 0) {
                TopBar(
                    userName: userName,
                    level: summary.level,
                    progress: summary.levelProgress,
                    advice: summary.advice,
                    showProfile: $showProfile,
                    showAdvice: $showAdvice
                )
                .padding(.horizontal, 18)
                .padding(.top, 10)
                .padding(.bottom, 8)

                TabView(selection: $tab) {
                    InicioView(summary: summary, funds: funds).tag(QuriTab.inicio)
                    AddView(funds: $funds, movements: $movements).tag(QuriTab.anadir)
                    FundsView(funds: $funds).tag(QuriTab.fondos)
                    FinanceView(summary: summary, funds: funds, movements: movements).tag(QuriTab.finanzas)
                    AnalysisView(summary: summary).tag(QuriTab.analisis)
                }
                .tabViewStyle(.page(indexDisplayMode: .never))

                BottomBar(tab: $tab)
                    .padding(.horizontal, 18)
                    .padding(.vertical, 8)
                    .background(.black.opacity(0.32))
            }
        }
        .sheet(isPresented: $showProfile) {
            ProfileSheet(userName: $userName, summary: summary)
                .presentationDetents([.medium])
                .presentationDragIndicator(.visible)
        }
        .alert("Consejo financiero", isPresented: $showAdvice) {
            Button("Entendido", role: .cancel) { }
        } message: {
            Text(summary.advice.detail)
        }
    }
}

private enum QuriTab: String, CaseIterable {
    case inicio = "Inicio"
    case anadir = "Anadir"
    case fondos = "Fondos"
    case finanzas = "Finanzas"
    case analisis = "Analisis"

    var icon: String {
        switch self {
        case .inicio: return "house"
        case .anadir: return "plus"
        case .fondos: return "folder"
        case .finanzas: return "wallet.pass"
        case .analisis: return "chart.line.uptrend.xyaxis"
        }
    }
}

private struct QuriFund: Identifiable, Hashable {
    let id = UUID()
    var name: String
    var target: Double
    var saved: Double
    var deadline: Date

    var progress: Double { target <= 0 ? 0 : min(saved / target, 1) }
    var completed: Bool { saved >= target }
    var remaining: Double { max(target - saved, 0) }
    var daysLeft: Int {
        max(Calendar.current.dateComponents([.day], from: Date(), to: deadline).day ?? 0, 0)
    }

    static let demo = [
        QuriFund(name: "Viaje", target: 800, saved: 320, deadline: Calendar.current.date(byAdding: .month, value: 3, to: Date()) ?? Date()),
        QuriFund(name: "Sofa", target: 500, saved: 500, deadline: Calendar.current.date(byAdding: .month, value: 1, to: Date()) ?? Date()),
        QuriFund(name: "Emergencia", target: 1000, saved: 424, deadline: Calendar.current.date(byAdding: .month, value: 8, to: Date()) ?? Date())
    ]
}

private struct QuriMovement: Identifiable, Hashable {
    enum Kind { case income, expense }
    enum Category: String, CaseIterable {
        case nomina = "Nomina"
        case hogar = "Hogar"
        case comida = "Comida"
        case ocio = "Ocio"
        case transporte = "Transporte"
    }

    let id = UUID()
    var title: String
    var amount: Double
    var kind: Kind
    var category: Category
    var necessary: Bool

    static let demo = [
        QuriMovement(title: "Nomina junio", amount: 1610, kind: .income, category: .nomina, necessary: true),
        QuriMovement(title: "Alquiler", amount: 620, kind: .expense, category: .hogar, necessary: true),
        QuriMovement(title: "Supermercado", amount: 290, kind: .expense, category: .comida, necessary: true),
        QuriMovement(title: "Ocio", amount: 180, kind: .expense, category: .ocio, necessary: false),
        QuriMovement(title: "Transporte", amount: 55, kind: .expense, category: .transporte, necessary: true)
    ]
}

private struct Advice {
    let title: String
    let detail: String
}

private struct QuriSummary {
    let funds: [QuriFund]
    let movements: [QuriMovement]

    var income: Double { movements.filter { $0.kind == .income }.reduce(0) { $0 + $1.amount } }
    var expense: Double { movements.filter { $0.kind == .expense }.reduce(0) { $0 + $1.amount } }
    var balance: Double { income - expense }
    var saved: Double { funds.reduce(0) { $0 + $1.saved } }
    var target: Double { funds.reduce(0) { $0 + $1.target } }
    var completed: Int { funds.filter(\.completed).count }
    var active: [QuriFund] { funds.filter { !$0.completed } }
    var globalProgress: Double { target <= 0 ? 0 : min(saved / target, 1) }
    var unnecessary: Double {
        movements.filter { $0.kind == .expense && !$0.necessary }.reduce(0) { $0 + $1.amount }
    }
    var levelProgress: Double {
        min((Double(funds.count) / 5 + Double(movements.count) / 8) / 2, 1)
    }
    var level: Int { max(1, Int(levelProgress * 10) + 1) }

    var advice: Advice {
        if expense > income {
            return Advice(title: "Gastos altos", detail: "Tus gastos superan tus ingresos este mes. Revisa gastos no necesarios antes de reforzar fondos.")
        }
        if unnecessary > expense * 0.25 && expense > 0 {
            return Advice(title: "Ojo con ocio", detail: "Una parte importante del gasto no es necesaria. Reducirla puede acelerar tus metas.")
        }
        if let urgent = active.sorted(by: { $0.daysLeft < $1.daysLeft }).first, urgent.daysLeft < 45 {
            return Advice(title: "Meta cercana", detail: "El fondo \(urgent.name) vence pronto. Prioriza aportaciones este mes.")
        }
        if completed > 0 {
            return Advice(title: "Buen avance", detail: "Ya tienes \(completed) fondo completado. Redirige nuevos ingresos a metas pendientes.")
        }
        return Advice(title: "Ahorra al cobrar", detail: "Separa el ahorro al recibir ingresos, antes de gastar.")
    }

    var expenseByCategory: [(String, Double)] {
        Dictionary(grouping: movements.filter { $0.kind == .expense }, by: { $0.category.rawValue })
            .map { ($0.key, $0.value.reduce(0) { $0 + $1.amount }) }
            .sorted { $0.1 > $1.1 }
    }
}

private struct TopBar: View {
    let userName: String
    let level: Int
    let progress: Double
    let advice: Advice
    @Binding var showProfile: Bool
    @Binding var showAdvice: Bool

    var body: some View {
        HStack(spacing: 12) {
            VStack(alignment: .leading, spacing: 8) {
                Text("Hola, \(userName) - Nivel \(level)")
                    .font(.caption.weight(.semibold))
                    .foregroundStyle(.white.opacity(0.82))

                ProgressView(value: progress)
                    .tint(.green)
                    .scaleEffect(x: 1, y: 1.8, anchor: .center)
                    .clipShape(Capsule())

                Button { showAdvice = true } label: {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(advice.title)
                            .font(.caption.weight(.bold))
                            .foregroundStyle(Color.quriGold)
                        Text(advice.detail)
                            .font(.caption2)
                            .foregroundStyle(.white)
                            .lineLimit(2)
                            .multilineTextAlignment(.leading)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                    .background(.black.opacity(0.25), in: RoundedRectangle(cornerRadius: 14))
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.quriGold.opacity(0.75), lineWidth: 1))
                }
                .buttonStyle(.plain)
            }

            Button { showProfile = true } label: {
                Image("AppLogo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 50, height: 50)
                    .clipShape(Circle())
                    .overlay(Circle().stroke(Color.quriGold, lineWidth: 1.5))
                    .shadow(color: Color.quriGold.opacity(0.45), radius: 10)
            }
            .accessibilityLabel("Abrir perfil")
        }
    }
}

private struct BottomBar: View {
    @Binding var tab: QuriTab

    var body: some View {
        HStack {
            ForEach(QuriTab.allCases, id: \.self) { item in
                Button { tab = item } label: {
                    Image(systemName: item.icon)
                        .font(.system(size: tab == item ? 25 : 22, weight: .semibold))
                        .foregroundStyle(tab == item ? Color.quriGold : .white.opacity(0.7))
                        .frame(maxWidth: .infinity)
                        .frame(height: 44)
                }
                .buttonStyle(.plain)
                .accessibilityLabel(item.rawValue)
            }
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 8)
        .background(.black.opacity(0.36), in: Capsule())
        .overlay(Capsule().stroke(Color.quriGold.opacity(0.35), lineWidth: 1))
    }
}

private struct InicioView: View {
    let summary: QuriSummary
    let funds: [QuriFund]

    var body: some View {
        ScreenScroll {
            BigBalance(summary: summary)
            LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                Metric(title: "Ingresos", value: summary.income, color: .green, icon: "arrow.down.left")
                Metric(title: "Gastos", value: summary.expense, color: .red, icon: "arrow.up.right")
                Metric(title: "Ahorrado", value: summary.saved, color: Color.quriGold, icon: "banknote")
                Metric(title: "Objetivo", value: summary.target, color: .cyan, icon: "target")
            }
            SummaryCard(summary: summary)
            FundsPreview(funds: funds)
        }
    }
}

private struct AddView: View {
    @Binding var funds: [QuriFund]
    @Binding var movements: [QuriMovement]
    @State private var isIncome = true
    @State private var title = "Salario"
    @State private var amount = "1300"
    @State private var selectedFund: UUID?
    @State private var allocation = "150"
    @State private var savedMessage = ""

    private var activeFunds: [QuriFund] { funds.filter { !$0.completed } }

    var body: some View {
        ScreenScroll {
            Text(isIncome ? "Anadir ingreso" : "Anadir gasto")
                .font(.largeTitle.weight(.bold))
                .foregroundStyle(.white)

            Picker("Tipo", selection: $isIncome) {
                Text("Gasto").tag(false)
                Text("Ingreso").tag(true)
            }
            .pickerStyle(.segmented)

            Field(title: "Concepto", text: $title)
            Field(title: "Cantidad total", text: $amount, keyboard: .decimalPad)

            if isIncome {
                Card {
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Distribucion a fondos")
                            .font(.headline)
                        ForEach(activeFunds) { fund in
                            Button { selectedFund = fund.id } label: {
                                HStack {
                                    VStack(alignment: .leading) {
                                        Text(fund.name).font(.body.weight(.bold))
                                        Text("\(fund.saved, format: .currency(code: "EUR")) / \(fund.target, format: .currency(code: "EUR"))")
                                            .font(.caption)
                                    }
                                    Spacer()
                                    if selectedFund == fund.id {
                                        Image(systemName: "checkmark.circle.fill").foregroundStyle(.green)
                                    }
                                }
                                .padding(12)
                                .background(selectedFund == fund.id ? Color.quriGold.opacity(0.85) : .black.opacity(0.22), in: RoundedRectangle(cornerRadius: 12))
                            }
                            .buttonStyle(.plain)
                        }
                        Field(title: "Cantidad para este fondo", text: $allocation, keyboard: .decimalPad)
                    }
                }
            }

            Button("Guardar") { save() }
                .font(.headline)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 14)
                .background(.green, in: Capsule())
                .foregroundStyle(.white)

            if !savedMessage.isEmpty {
                Text(savedMessage).foregroundStyle(.green).font(.headline)
            }
        }
        .onAppear { selectedFund = activeFunds.first?.id }
    }

    private func save() {
        let parsed = Double(amount.replacingOccurrences(of: ",", with: ".")) ?? 0
        guard parsed > 0 else { return }
        movements.insert(
            QuriMovement(title: title, amount: parsed, kind: isIncome ? .income : .expense, category: isIncome ? .nomina : .ocio, necessary: !isIncome),
            at: 0
        )
        if isIncome, let selectedFund, let value = Double(allocation.replacingOccurrences(of: ",", with: ".")),
           let index = funds.firstIndex(where: { $0.id == selectedFund && !$0.completed }) {
            funds[index].saved = min(funds[index].saved + value, funds[index].target)
        }
        savedMessage = "Movimiento guardado"
    }
}

private struct FundsView: View {
    @Binding var funds: [QuriFund]

    var body: some View {
        ScreenScroll {
            HStack {
                Text("Fondos").font(.largeTitle.weight(.bold))
                Spacer()
                Button { addFund() } label: {
                    Image(systemName: "plus")
                        .font(.title3.weight(.bold))
                        .foregroundStyle(.black)
                        .frame(width: 42, height: 42)
                        .background(Color.quriGold, in: Circle())
                }
            }
            .foregroundStyle(.white)

            ForEach($funds) { $fund in
                Card {
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text(fund.name).font(.headline)
                            Spacer()
                            if fund.completed {
                                Text("Completado")
                                    .font(.caption.weight(.bold))
                                    .foregroundStyle(.green)
                            }
                        }
                        ProgressView(value: fund.progress).tint(fund.completed ? .green : Color.quriGold)
                        HStack {
                            Text(fund.saved, format: .currency(code: "EUR"))
                            Spacer()
                            Text("\(Int(fund.progress * 100))%")
                        }
                        .font(.subheadline.weight(.semibold))
                    }
                }
            }
        }
    }

    private func addFund() {
        funds.append(QuriFund(name: "Nuevo fondo", target: 300, saved: 0, deadline: Calendar.current.date(byAdding: .month, value: 4, to: Date()) ?? Date()))
    }
}

private struct FinanceView: View {
    let summary: QuriSummary
    let funds: [QuriFund]
    let movements: [QuriMovement]

    var body: some View {
        ScreenScroll {
            Text("Plan mensual")
                .font(.largeTitle.weight(.bold))
                .foregroundStyle(.white)

            Card {
                VStack(alignment: .leading, spacing: 10) {
                    Text("Ingresos detectados").font(.headline)
                    ForEach(movements.filter { $0.kind == .income }) { income in
                        HStack {
                            Text(income.title)
                            Spacer()
                            Text(income.amount, format: .currency(code: "EUR")).foregroundStyle(.green)
                        }
                    }
                }
            }

            Card {
                VStack(alignment: .leading, spacing: 10) {
                    Text("Reparto seguro").font(.headline)
                    Row("Total repartido", summary.saved, Color.quriGold)
                    Row("Disponible restante", summary.balance, summary.balance >= 0 ? .green : .red)
                    Text(summary.advice.detail)
                        .font(.subheadline)
                        .foregroundStyle(.white.opacity(0.82))
                }
            }

            Card {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Fondos actualizados").font(.headline)
                    ForEach(funds) { fund in
                        HStack {
                            VStack(alignment: .leading) {
                                Text(fund.name)
                                Text(fund.completed ? "Completado" : "Faltan \(fund.remaining, format: .currency(code: "EUR"))")
                                    .font(.caption)
                                    .foregroundStyle(fund.completed ? .green : .white.opacity(0.7))
                            }
                            Spacer()
                            Text("\(Int(fund.progress * 100))%").foregroundStyle(Color.quriGold)
                        }
                    }
                }
            }
        }
    }

    @ViewBuilder private func Row(_ title: String, _ value: Double, _ color: Color) -> some View {
        HStack {
            Text(title)
            Spacer()
            Text(value, format: .currency(code: "EUR")).foregroundStyle(color)
        }
    }
}

private struct AnalysisView: View {
    let summary: QuriSummary

    var body: some View {
        ScreenScroll {
            Text("Analisis")
                .font(.largeTitle.weight(.bold))
                .foregroundStyle(.white)

            Card {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Distribucion de gastos").font(.headline)
                    Donut(values: summary.expenseByCategory.map(\.1))
                        .frame(height: 190)
                    ForEach(summary.expenseByCategory, id: \.0) { item in
                        HStack {
                            Text(item.0)
                            Spacer()
                            Text(item.1, format: .currency(code: "EUR"))
                        }
                    }
                }
            }

            Card {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Calidad del gasto").font(.headline)
                    Ring(progress: max(0, 1 - summary.unnecessary / max(summary.expense, 1)), color: .green, text: "Salud")
                        .frame(height: 145)
                    Text("Gasto no necesario: \(summary.unnecessary, format: .currency(code: "EUR"))")
                        .foregroundStyle(Color.quriGold)
                }
            }
        }
    }
}

private struct ProfileSheet: View {
    @Binding var userName: String
    let summary: QuriSummary

    var body: some View {
        ZStack {
            Color.quriDeepGreen.ignoresSafeArea()
            VStack(alignment: .leading, spacing: 18) {
                Text("Perfil").font(.title.bold()).foregroundStyle(Color.quriGold)
                TextField("Nombre", text: $userName).textFieldStyle(.roundedBorder)
                Text("Nivel \(summary.level)").font(.headline).foregroundStyle(.white)
                ProgressView(value: summary.levelProgress).tint(.green)
                Text(SharedStatus.text).font(.footnote).foregroundStyle(.white.opacity(0.75))
                Spacer()
            }
            .padding(24)
        }
    }
}

private struct BigBalance: View {
    let summary: QuriSummary

    var body: some View {
        Card {
            VStack(alignment: .leading, spacing: 12) {
                HStack {
                    Text("Balance total").font(.headline)
                    Spacer()
                    Text("Este mes")
                        .font(.caption.weight(.semibold))
                        .foregroundStyle(Color.quriGold)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 6)
                        .overlay(Capsule().stroke(Color.quriGold.opacity(0.75), lineWidth: 1))
                }
                Text(summary.balance, format: .currency(code: "EUR"))
                    .font(.system(size: 42, weight: .black, design: .rounded))
                    .foregroundStyle(summary.balance >= 0 ? .green : .red)
                Sparkline(values: [160, 260, 210, 420, 360, 510, summary.balance])
                    .frame(height: 76)
            }
        }
    }
}

private struct Metric: View {
    let title: String
    let value: Double
    let color: Color
    let icon: String

    var body: some View {
        Card {
            VStack(alignment: .leading, spacing: 10) {
                Image(systemName: icon).foregroundStyle(color).font(.title2.weight(.bold))
                Text(title).font(.subheadline)
                Text(value, format: .currency(code: "EUR")).font(.title3.weight(.bold))
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}

private struct SummaryCard: View {
    let summary: QuriSummary

    var body: some View {
        Card {
            HStack {
                VStack(alignment: .leading, spacing: 7) {
                    Text("Fondos de ahorro").font(.headline)
                    Text("Activos: \(summary.active.count)")
                    Text("Completados: \(summary.completed)").foregroundStyle(Color.quriGold)
                    Text("Ahorrado: \(summary.saved, format: .currency(code: "EUR"))")
                }
                Spacer()
                Ring(progress: summary.globalProgress, color: .green, text: "\(Int(summary.globalProgress * 100))%")
                    .frame(width: 92, height: 92)
            }
        }
    }
}

private struct FundsPreview: View {
    let funds: [QuriFund]

    var body: some View {
        Card {
            VStack(alignment: .leading, spacing: 12) {
                Text("Fondos activos").font(.headline)
                ForEach(Array(funds.prefix(3))) { fund in
                    VStack(alignment: .leading, spacing: 6) {
                        HStack {
                            Text(fund.name)
                            Spacer()
                            Text(fund.saved, format: .currency(code: "EUR")).foregroundStyle(Color.quriGold)
                        }
                        ProgressView(value: fund.progress).tint(fund.completed ? .green : Color.quriGold)
                    }
                }
            }
        }
    }
}

private struct ScreenScroll<Content: View>: View {
    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        ScrollView(showsIndicators: false) {
            VStack(alignment: .leading, spacing: 16) {
                content
            }
            .padding(.horizontal, 18)
            .padding(.bottom, 24)
        }
    }
}

private struct Card<Content: View>: View {
    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        content
            .padding(16)
            .foregroundStyle(.white)
            .background(.black.opacity(0.22), in: RoundedRectangle(cornerRadius: 18))
            .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.quriGold.opacity(0.7), lineWidth: 1))
    }
}

private struct Field: View {
    let title: String
    @Binding var text: String
    var keyboard: UIKeyboardType = .default

    var body: some View {
        TextField(title, text: $text)
            .keyboardType(keyboard)
            .padding(14)
            .background(.black.opacity(0.18), in: RoundedRectangle(cornerRadius: 14))
            .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.quriGold, lineWidth: 1))
            .foregroundStyle(.white)
    }
}

private struct QuriBackground: View {
    var body: some View {
        LinearGradient(colors: [.black, .quriDeepGreen, .black], startPoint: .topLeading, endPoint: .bottomTrailing)
            .overlay {
                RadialGradient(colors: [.green.opacity(0.35), .clear], center: .center, startRadius: 12, endRadius: 320)
            }
    }
}

private struct Sparkline: View {
    let values: [Double]

    var body: some View {
        GeometryReader { proxy in
            let maxValue = max(values.max() ?? 1, 1)
            let minValue = values.min() ?? 0
            let range = max(maxValue - minValue, 1)
            Path { path in
                for index in values.indices {
                    let x = proxy.size.width * CGFloat(index) / CGFloat(max(values.count - 1, 1))
                    let y = proxy.size.height - proxy.size.height * CGFloat((values[index] - minValue) / range)
                    index == 0 ? path.move(to: CGPoint(x: x, y: y)) : path.addLine(to: CGPoint(x: x, y: y))
                }
            }
            .stroke(.green, style: StrokeStyle(lineWidth: 3, lineCap: .round, lineJoin: .round))
        }
    }
}

private struct Ring: View {
    let progress: Double
    let color: Color
    let text: String

    var body: some View {
        ZStack {
            Circle().stroke(.white.opacity(0.12), lineWidth: 12)
            Circle()
                .trim(from: 0, to: progress)
                .stroke(color, style: StrokeStyle(lineWidth: 12, lineCap: .round))
                .rotationEffect(.degrees(-90))
            Text(text).font(.headline.weight(.bold)).foregroundStyle(.white)
        }
    }
}

private struct Donut: View {
    let values: [Double]
    private let colors: [Color] = [.green, Color.quriGold, .red, .cyan, .purple]

    var body: some View {
        GeometryReader { proxy in
            let total = max(values.reduce(0, +), 1)
            let size = min(proxy.size.width, proxy.size.height)
            ZStack {
                ForEach(values.indices, id: \.self) { index in
                    let start = values.prefix(index).reduce(0, +) / total
                    let end = values.prefix(index + 1).reduce(0, +) / total
                    Circle()
                        .trim(from: start, to: end)
                        .stroke(colors[index % colors.count], style: StrokeStyle(lineWidth: 34, lineCap: .butt))
                        .rotationEffect(.degrees(-90))
                        .frame(width: size - 34, height: size - 34)
                }
                Text("Total").font(.headline).foregroundStyle(.white)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

private extension Color {
    static let quriGold = Color(red: 0.88, green: 0.66, blue: 0.08)
    static let quriDeepGreen = Color(red: 0.0, green: 0.12, blue: 0.07)
}

#Preview {
    ContentView()
}

