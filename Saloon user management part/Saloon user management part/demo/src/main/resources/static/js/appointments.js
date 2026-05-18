// Shared Appointment JS Logic
document.addEventListener('DOMContentLoaded', function() {
    // Initialize Flatpickr for Date
    flatpickr(".datepicker", {
        minDate: "today",
        dateFormat: "Y-m-d",
        altInput: true,
        altFormat: "F j, Y",
        theme: "dark"
    });

    // Time Slot Selection logic
    const timeSlots = document.querySelectorAll('.time-slot');
    const hiddenTimeInput = document.getElementById('selectedTime');
    const dateInput = document.querySelector('.datepicker');
    const stylistSelect = document.querySelector('select[name="stylistName"]');
    
    function updateAvailability() {
        const date = dateInput.value;
        const stylist = stylistSelect.value;
        
        if (date && stylist) {
            fetch(`/appointments/booked-times?date=${date}&stylist=${stylist}`)
                .then(response => response.json())
                .then(bookedTimes => {
                    timeSlots.forEach(slot => {
                        const slotTime = slot.getAttribute('data-time');
                        // Backend returns times as "HH:mm:ss" or "HH:mm", we need to match
                        const isBooked = bookedTimes.some(bt => bt.startsWith(slotTime));
                        
                        if (isBooked) {
                            slot.classList.add('disabled');
                            if (slot.classList.contains('active')) {
                                slot.classList.remove('active');
                                if (hiddenTimeInput) hiddenTimeInput.value = '';
                            }
                        } else {
                            slot.classList.remove('disabled');
                        }
                    });
                });
        }
    }

    if (dateInput) dateInput.addEventListener('change', updateAvailability);
    if (stylistSelect) stylistSelect.addEventListener('change', updateAvailability);

    timeSlots.forEach(slot => {
        slot.addEventListener('click', function() {
            if (this.classList.contains('disabled')) return;
            timeSlots.forEach(s => s.classList.remove('active'));
            this.classList.add('active');
            if (hiddenTimeInput) hiddenTimeInput.value = this.getAttribute('data-time');
            const error = document.getElementById('timeError');
            if (error) error.style.setProperty('display', 'none', 'important');
        });
    });

    // Bootstrap form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            const timeVal = hiddenTimeInput ? hiddenTimeInput.value : '';
            if (!form.checkValidity() || (hiddenTimeInput && !timeVal)) {
                event.preventDefault();
                event.stopPropagation();
                const error = document.getElementById('timeError');
                if (hiddenTimeInput && !timeVal && error) {
                    error.style.setProperty('display', 'block', 'important');
                }
            }
            form.classList.add('was-validated');
        }, false);
    });
});

function toggleVIP() {
    const typeSelect = document.getElementById('typeSelect');
    const vipSection = document.getElementById('vipSection');
    if(typeSelect && vipSection) {
        if(typeSelect.value === 'VIP') {
            vipSection.style.display = 'block';
        } else {
            vipSection.style.display = 'none';
        }
    }
}
