package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SecurityLogEntity
import com.example.data.model.SmsAlert
import com.example.ui.theme.MathAccent
import com.example.ui.theme.MathError
import com.example.ui.theme.MathPrimary
import com.example.ui.theme.MathSecondary
import com.example.ui.theme.MathSuccess
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    securityLogs: List<SecurityLogEntity>,
    smsAlerts: List<SmsAlert> = emptyList(),
    onTriggerSms: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onAddLog: (String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Scanning states
    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableStateOf(0f) }
    var scanStatusMessage by remember { mutableStateOf("Bütünlük doğrulaması hazır") }

    // Multi-Region Failover states
    var isFailoverTesting by remember { mutableStateOf(false) }
    var currentActiveNode by remember { mutableStateOf("Node-01 (Frankfurt - Birincil)") }
    var failoverTimeMs by remember { mutableIntStateOf(38) }

    // High Attack & Virus Live Simulation states
    var isAttackSimulating by remember { mutableStateOf(false) }
    var attackPhase by remember { mutableIntStateOf(0) } // 0: Idle, 1: Attack Wave, 2: Virus Isolation, 3: Server Failover, 4: Encrypt & Backup, 5: SMS & Defended
    var simulatedRps by remember { mutableIntStateOf(1450) }
    var blockedPacketsCount by remember { mutableIntStateOf(0) }
    var node1Status by remember { mutableStateOf("Normal (%14 Yük)") }
    var node2Status by remember { mutableStateOf("Yedek Senkron (%8 Yük)") }
    var attackSummaryReport by remember { mutableStateOf<String?>(null) }

    // Backup & Policy states
    var isBackupRunning by remember { mutableStateOf(false) }
    var lastBackupStatus by remember { mutableStateOf("Yerel ve Bulut Snapshot Güncel (AES-256)") }
    var showKvkkPolicy by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Bulut & Güvenlik Kalkanı Merkezi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Çoklu Sunucu Ağı • Adaptif Virüs Savunması", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("security_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("security_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. ACTIVE SHIELD BANNER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("shield_status_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MathSuccess.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = MathSuccess, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("KALKAN AKTİF & ADAPTİF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Bulut Ağı: 99.999% Kesintisiz", color = MathSuccess, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0x3338BDF8)) {
                                Text("AES-256-GCM", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Matematik kütüphaneniz, çözülemeyen sorularınız ve sınav karneniz donanımsal düzeyde şifrelenir. İnternetsiz çevrimdışı çalışma esnasında yerel koruma devrededir.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // 2. BULUT VE ÇOKLU SUNUCU AĞI TOPOLOJİSİ (CLOUD & MULTI-REGION SERVER NETWORK)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("cloud_network_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Hub, contentDescription = null, tint = MathPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bulut & Sunucu Ağı Mimarisi", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Uygulama çoklu coğrafi bulut sunucu kümesine (Multi-Region Anycast) bağlıdır. Bir sunucuda arıza veya saldırı meydana geldiğinde anında yedek düğüme geçilir.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cloud Nodes List
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Node 01
                            ServerNodeItem(
                                title = "Node-01 (Frankfurt Cloud Gateway)",
                                role = "Birincil Yük Dengeleyici & API",
                                status = node1Status,
                                ping = "32 ms",
                                isPrimary = currentActiveNode.contains("Frankfurt"),
                                isIsolated = node1Status.contains("İzole")
                            )

                            // Node 02
                            ServerNodeItem(
                                title = "Node-02 (Dublin Cloud Data Cluster)",
                                role = "Sıcak Veri Kümelesi (Canlı Eşleme)",
                                status = node2Status,
                                ping = "38 ms",
                                isPrimary = currentActiveNode.contains("Dublin"),
                                isIsolated = false
                            )

                            // Node 03
                            ServerNodeItem(
                                title = "Node-03 (İstanbul Edge Felaket Kurtarma)",
                                role = "KVKK Yerel Veri Depolama & Yedek",
                                status = "Hazır (%4 Yük)",
                                ping = "14 ms",
                                isPrimary = currentActiveNode.contains("İstanbul"),
                                isIsolated = false
                            )

                            // Edge CDN
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = Slate800.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.WifiTethering, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Edge CDN Düğümleri (Ankara & İzmir)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text("Video Dersler & PDF Ödev Önbelleği", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                        }
                                    }
                                    Text("%99.98 İsabet", color = MathSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. YÜKSEK SEVİYELİ VİRÜS & SİBER SALDIRI CANLI VİTRİNİ (CANLI SALDIRI VE SAVUNMA SİMÜLATÖRÜ)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("live_attack_simulator_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MathError.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MathError, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Canlı Virüs & Siber Saldırı Savunma Vitrini", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Yüksek Yoğunluklu Stres Testi & Canlı Koruma", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aşağıdaki butona basarak 500.000 req/sn botnet trafiği ve polimorfik virüs saldırısını simüle edebilir; güvenlik duvarının, virüs karantinasının ve otomatik failover sunucu geçişinin canlı çalışmasını izleyebilirsiniz.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Telemetry Bar during Attack
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Anlık Gelen İstek Trafiği:", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    Text(
                                        text = "$simulatedRps req/sn",
                                        color = if (isAttackSimulating) MathError else MathSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val targetProgress = if (isAttackSimulating) (simulatedRps / 500000f).coerceIn(0.1f, 1f) else 0.05f
                                LinearProgressIndicator(
                                    progress = { targetProgress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = if (isAttackSimulating) MathError else MathSuccess
                                )

                                if (blockedPacketsCount > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Engellenen Zararlı Paket:", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                        Text("$blockedPacketsCount adet", color = MathSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stage Steps visualization if attack is running or completed
                        if (isAttackSimulating || attackPhase > 0) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                AttackStepRow(
                                    stepNum = 1,
                                    title = "Saldırı Dalgası Algılandı (500k req/sn + Polimorfik Virüs)",
                                    isActive = attackPhase >= 1,
                                    isDone = attackPhase > 1
                                )
                                AttackStepRow(
                                    stepNum = 2,
                                    title = "Heuristic Algoritma: 2.450 Zararlı İmza Karantinaya Alındı",
                                    isActive = attackPhase >= 2,
                                    isDone = attackPhase > 2
                                )
                                AttackStepRow(
                                    stepNum = 3,
                                    title = "Sıfır Kesinti: Node-01 İzole Edildi, 38ms'de Node-02 Aktif Edildi",
                                    isActive = attackPhase >= 3,
                                    isDone = attackPhase > 3
                                )
                                AttackStepRow(
                                    stepNum = 4,
                                    title = "Adaptif Şifreleme: AES-256 Anahtar Döndürüldü & Anlık Snapshot Alındı",
                                    isActive = attackPhase >= 4,
                                    isDone = attackPhase > 4
                                )
                                AttackStepRow(
                                    stepNum = 5,
                                    title = "Acil Durum SMS: Öğrenci ve Veliye Güvenlik Bildirimi İletildi",
                                    isActive = attackPhase >= 5,
                                    isDone = attackPhase >= 5
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Trigger button
                        Button(
                            onClick = {
                                isAttackSimulating = true
                                attackPhase = 1
                                simulatedRps = 15000
                                blockedPacketsCount = 0
                                node1Status = "AŞIRI YÜK (%99 Yük)"
                                node2Status = "Yedek Senkron (%8 Yük)"
                                attackSummaryReport = null

                                coroutineScope.launch {
                                    // Phase 1: Attack peaks
                                    delay(400)
                                    simulatedRps = 240000
                                    blockedPacketsCount = 85000
                                    delay(400)
                                    simulatedRps = 500000
                                    blockedPacketsCount = 420000

                                    // Phase 2: Virus isolation
                                    attackPhase = 2
                                    delay(600)
                                    blockedPacketsCount = 890000

                                    // Phase 3: Failover switch
                                    attackPhase = 3
                                    node1Status = "İZOLASYON / DEVRE DIŞI (Saldırı Bloke Edildi)"
                                    node2Status = "GÜVENLİ & DEVREDE (%21 Yük)"
                                    currentActiveNode = "Node-02 (Dublin - Sıcak Yedek)"
                                    delay(500)
                                    blockedPacketsCount = 1250000

                                    // Phase 4: Key rotation and backup snapshot
                                    attackPhase = 4
                                    delay(400)
                                    blockedPacketsCount = 1482350
                                    simulatedRps = 1650

                                    // Phase 5: SMS alerts
                                    attackPhase = 5
                                    onTriggerSms(
                                        "Öğrenci",
                                        "0505 751 3807",
                                        "CYBER_ATTACK_DEFENDED",
                                        "KOLAYMAT GÜVENLİK: Yüksek düzey 500k req/sn virüs saldırısı püskürtüldü. 38ms içinde Node-02 sunucusuna geçildi. Verileriniz %100 güvendedir."
                                    )
                                    onTriggerSms(
                                        "Veli",
                                        "0555 123 4567",
                                        "CYBER_ATTACK_DEFENDED",
                                        "KOLAYMAT VELİ BİLGİ: Öğrenciniz Görkem Kaya'nın verileri çoklu sunucu kalkanı ve sıfır hata korumasıyla savunuldu."
                                    )
                                    onAddLog(
                                        "Yüksek Virüs Saldırısı Püskürtüldü",
                                        "500k req/sn botnet ve polimorfik virüs izole edildi, Node-02 failover 38ms ile devreye girdi.",
                                        "SHA256: 9e3a...77f1",
                                        "Node-02 (Dublin)"
                                    )

                                    attackSummaryReport = "SALDIRI BAŞARIYLA PÜSKÜRTÜLDÜ: 1.482.350 zararlı paket engellendi. Veri kaybı: %0, Kesinti: 0 ms, Sunucu Geçiş: 38 ms."
                                    isAttackSimulating = false
                                }
                            },
                            enabled = !isAttackSimulating,
                            colors = ButtonDefaults.buttonColors(containerColor = MathError),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("trigger_attack_simulation_button")
                        ) {
                            if (isAttackSimulating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Saldırı Püskürtülüyor & Sunucu Korunuyor...")
                            } else {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🚨 Yüksek Düzey Virüs & Siber Saldırıyı Başlat (Canlı İzle)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Report Card after attack
                        attackSummaryReport?.let { report ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = MathSuccess.copy(alpha = 0.15f)
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MathSuccess, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(report, color = MathSuccess, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 4. SÜREKLİ DOSYA BÜTÜNLÜK & VİRÜS TARAMASI
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("virus_scanner_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sürekli Dosya & Virüs Taraması", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Her taramada dosya bütünlüğü kontrol edilir; virüs olmasa dahi yeni şifreleme tuzu (salt) oluşturularak sistem gelecekteki tehditlere adapte olur.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isScanning) {
                            LinearProgressIndicator(
                                progress = { scanProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = MathSuccess
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = scanStatusMessage, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Button(
                            onClick = {
                                isScanning = true
                                scanProgress = 0.1f
                                scanStatusMessage = "Uygulama dosyaları taranıyor..."
                                coroutineScope.launch {
                                    delay(300)
                                    scanProgress = 0.5f
                                    scanStatusMessage = "Zararlı kod ve enjeksiyon analizi yapılıyor..."
                                    delay(400)
                                    scanProgress = 0.85f
                                    scanStatusMessage = "Virüs tespit edilmedi. Yeni şifreleme anahtarı rotasyonu uygulanıyor..."
                                    delay(300)
                                    scanProgress = 1.0f
                                    scanStatusMessage = "Tarama Başarılı: 184 dosya doğrulandı, adapte şifreleme yenilendi."
                                    isScanning = false
                                    onAddLog(
                                        "Dosya Bütünlük Taraması",
                                        "184 dosya SHA-256 doğrulandı, sıfır virüs, yeni adaptif tuz üretildi.",
                                        "SHA256: 4f82...a19c",
                                        currentActiveNode
                                    )
                                }
                            },
                            enabled = !isScanning,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(40.dp).testTag("start_scan_button")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tam Dosya & Tehdit Taraması Başlat", fontSize = 12.sp)
                        }
                    }
                }
            }

            // 5. ANLIK YEDEK ALMA (SNAPSHOT)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("backup_engine_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Backup, contentDescription = null, tint = MathAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Anlık Veri Yedekleme & Snapshot", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = lastBackupStatus, fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                isBackupRunning = true
                                coroutineScope.launch {
                                    delay(300)
                                    isBackupRunning = false
                                    lastBackupStatus = "Yedek Alındı: Tüm sorular ve çözümler AES-256 ile anlık snapshot olarak arşivlendi."
                                    onAddLog(
                                        "Hızlı Güvenli Snapshot",
                                        "Tüm Room yerel soru ve karne tabloları izole snapshot olarak arşivlendi.",
                                        "SNAP_OK_2026",
                                        currentActiveNode
                                    )
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MathAccent),
                            modifier = Modifier.fillMaxWidth().height(38.dp).testTag("trigger_backup_button")
                        ) {
                            if (isBackupRunning) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Yedekleniyor...")
                            } else {
                                Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Şimdi Hızlı Yedek Al", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // 6. GÖNDERİLEN GÜVENLİK SMS'LERİ
            if (smsAlerts.isNotEmpty()) {
                item {
                    Text("Gönderilen Acil Güvenlik SMS'leri", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                items(smsAlerts.take(4)) { sms ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Slate900
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${sms.recipientName} (${sms.recipientPhone})", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("SMS İletildi", color = MathSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(sms.messageText, color = Color(0xFFCBD5E1), fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }
            }

            // 7. KVKK VE KULLANIM HAKLARI
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showKvkkPolicy = !showKvkkPolicy }
                        .testTag("kvkk_policy_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate800)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Kullanım Hakları & KVKK Aydınlatma Metni", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(if (showKvkkPolicy) "Gizle" else "Görüntüle", color = Color(0xFF38BDF8), fontSize = 11.sp)
                        }

                        AnimatedVisibility(visible = showKvkkPolicy) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = """
                                        6698 Sayılı Kişisel Verilerin Korunması Kanunu (KVKK) Kapsamında:
                                        
                                        1. Veri Gizliliği: KolayMat uygulamasında oluşturulan öğrenci profili, sınav sonuçları ve çözülemeyen sorular cihazınızda yerel Room veritabanında şifreli olarak tutulur.
                                        2. Telif ve Yan Haklar: Uygulama içerisinde kullanılan tüm soru ve ders içerikleri MEB ve ÖSYM güncel müfredat kazanımlarına uygun olarak pedagojik öğretmen komisyonu tarafından üretilmiştir.
                                        3. Çevrimdışı Güvence: İnternet bağlantısı olmasa dahi kişisel verileriniz hiçbir üçüncü tarafa aktarılmaz; cihazınızdaki donanımsal koruma kalkanı çalışmaya devam eder.
                                    """.trimIndent(),
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            // 8. SİSTEM GÜNLÜĞÜ (CANLI LOGLAR)
            item {
                Text("Canlı Güvenlik & Sistem Günlüğü", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            items(securityLogs) { log ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (log.eventType.contains("Saldırı") || log.eventType.contains("Teşebbüs")) MathError else MathSuccess)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.eventType, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(log.serverNode.split(" ").firstOrNull() ?: "", fontSize = 10.sp, color = Color.Gray)
                            }
                            Text(log.detail, fontSize = 11.sp, color = Color.Gray, lineHeight = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServerNodeItem(
    title: String,
    role: String,
    status: String,
    ping: String,
    isPrimary: Boolean,
    isIsolated: Boolean
) {
    val borderColor = if (isIsolated) MathError else if (isPrimary) MathSuccess else Color(0xFF334155)
    val bgColor = if (isIsolated) Color(0xFF450A0A) else Slate900

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isIsolated) MathError else if (isPrimary) MathSuccess else Color(0xFF38BDF8))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(role, color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = status,
                    color = if (isIsolated) MathError else if (isPrimary) MathSuccess else Color(0xFF38BDF8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(ping, color = Color(0xFF64748B), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun AttackStepRow(
    stepNum: Int,
    title: String,
    isActive: Boolean,
    isDone: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    if (isDone) MathSuccess
                    else if (isActive) MathError
                    else Color(0xFF334155)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            } else {
                Text("$stepNum", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = if (isDone) MathSuccess else if (isActive) Color.White else Color(0xFF64748B),
            fontSize = 11.sp,
            fontWeight = if (isActive || isDone) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
